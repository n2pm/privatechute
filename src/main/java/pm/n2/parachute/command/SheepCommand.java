package pm.n2.parachute.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import pm.n2.parachute.Parachute;
import pm.n2.parachute.mixin.IMixinClientConnection;

public class SheepCommand {
    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<CauldronClientCommandSource> command = ClientCommandManager.literal("sheep")
                .executes((context) -> execute(context.getSource()));
        dispatcher.register(command);
    }

    public static void warp(Vec3d origPos, Vec3d targetPos) {
        var mc = MinecraftClient.getInstance();
        var player = mc.player;

        var distance = origPos.distanceTo(targetPos);
        var teleportCount = Math.ceil(distance / 5);

        var deltaPos = targetPos.subtract(origPos);
        var movePerTeleport = deltaPos.multiply(1 / teleportCount);
        Parachute.LOGGER.info("delta: {} {} {}", deltaPos.x, deltaPos.y, deltaPos.z);

        var playerX = origPos.x;
        var playerY = origPos.y;
        var playerZ = origPos.z;

        for (var i = 0; i < teleportCount; i++) {
            var deltaX = movePerTeleport.x;
            var deltaY = movePerTeleport.y;
            var deltaZ = movePerTeleport.z;

            Parachute.LOGGER.info("player: {} {} {}", playerX, playerY, playerZ);

            var movePacket = new PlayerMoveC2SPacket.Full(
                    playerX + deltaX,
                    playerY + deltaY,
                    playerZ + deltaZ,
                    90, 90, true);

            var connection = (IMixinClientConnection) player.networkHandler.getConnection();
            connection.invokeSendImmediately(movePacket, null);
            Parachute.LOGGER.info("sent, doing {} more", teleportCount - i - 1);

            playerX += deltaX;
            playerY += deltaY;
            playerZ += deltaZ;

            try {
                Thread.sleep(1000 / 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int execute(CauldronClientCommandSource source) {
        var mc = MinecraftClient.getInstance();
        var player = mc.player;

        //var baseX = 2105584;
        //var baseY = -35;
        //var baseZ = 2105296;
        //var entityType = SheepEntity.class;

        var baseX = -48;
        var baseY = 62;
        var baseZ = -5;
        var entityType = PlayerEntity.class;

        var coords = new BlockPos(baseX - 10, baseY - 10, baseZ - 10);
        var coords2 = new BlockPos(baseX + 10, baseY + 10, baseZ + 10);
        var box = new Box(coords, coords2);

        var sheeps = mc.world.getEntitiesByClass(entityType, box, (entity) -> entity != mc.player);

        if (sheeps.size() > 0) {
            var sheep = sheeps.get(0);

            var sheepPos = sheep.getPos();
            var origPos = player.getPos();
            Parachute.LOGGER.info("sheep pos: {} {} {}", sheepPos.getX(), sheepPos.getY(), sheepPos.getZ());
            Parachute.LOGGER.info("orig pos: {} {} {}", origPos.getX(), origPos.getY(), origPos.getZ());

            new Thread(() -> {
                Parachute.LOGGER.info("warping to sheep");
                warp(origPos, sheepPos);

                Parachute.LOGGER.info("merking sheep");
                player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(sheep, player.isSneaking()));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Parachute.LOGGER.info("warping back");
                warp(sheepPos, origPos);

                Parachute.LOGGER.info("done");
            }).start();
        } else {
            Parachute.LOGGER.info("no sheep found");
        }

        return 1;
    }
}
