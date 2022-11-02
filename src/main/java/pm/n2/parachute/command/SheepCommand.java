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
import pm.n2.parachute.Parachute;
import pm.n2.parachute.mixin.IMixinClientConnection;

public class SheepCommand {
    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<CauldronClientCommandSource> command = ClientCommandManager.literal("sheep")
                .executes((context) -> execute(context.getSource()));
        dispatcher.register(command);
    }

    public static int execute(CauldronClientCommandSource source) {
        var mc = MinecraftClient.getInstance();

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

            // get the sheep's position
            var pos = sheep.getPos();
            Parachute.LOGGER.info("{} {} {}", pos.getX(), pos.getY(), pos.getZ());

            // teleport the player to the sheep
            var player = mc.player;
            var movementPacket = new PlayerMoveC2SPacket.Full(pos.getX(), pos.getY(), pos.getZ(), 90, 90, true);
            player.networkHandler.sendPacket(movementPacket);

            var distance = player.distanceTo(sheep);
            var teleportCount = Math.ceil(distance / 3.5);
            Parachute.LOGGER.info("distance: {}, teleportCount: {}", distance, teleportCount);

            var deltaPos = sheep.getPos().subtract(player.getPos());
            var movePerTeleport = deltaPos.multiply(1 / teleportCount);
            Parachute.LOGGER.info("deltaPos: {} {} {}", deltaPos.getX(), deltaPos.getY(), deltaPos.getZ());
            Parachute.LOGGER.info("movePerTeleport: {} {} {}", movePerTeleport.getX(), movePerTeleport.getY(), movePerTeleport.getZ());

            var playerX = player.getX();
            var playerY = player.getY();
            var playerZ = player.getZ();
            for (int i = 0; i < teleportCount; i++) {
                var deltaX = movePerTeleport.getX();
                var deltaY = movePerTeleport.getY();
                var deltaZ = movePerTeleport.getZ();

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
            }

            Parachute.LOGGER.info("merking sheep");
            player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(sheep, player.isSneaking()));
        }
        return 1;
    }
}
