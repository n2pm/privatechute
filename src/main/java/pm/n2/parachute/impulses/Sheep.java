package pm.n2.parachute.impulses;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import pm.n2.parachute.Parachute;
import pm.n2.parachute.mixin.IMixinClientConnection;

import java.util.ArrayList;
import java.util.List;

// any sheepers in chat? [USER WAS BANNED FOR THIS POST]
public class Sheep {

    private static boolean warping = false;

    private static Vec3d movePerTeleport;
    private static Vec3d playerPos;
    private static Vec3d sheepPos;
    private static Vec3d origPos;

    private static LivingEntity sheep;
    private static Stage stage;

    private static enum Stage {
        TOWARDS,
        ATTACK,
        RETURN
    }

    private static double teleportCount;
    private static int teleportsLeft;
    private static boolean fuck = false;

    private static List<Packet<?>> packets = new ArrayList<>();

    private static void warp() {
        var client = MinecraftClient.getInstance();
        var player = client.player;

        var deltaX = movePerTeleport.x;
        var deltaY = movePerTeleport.y;
        var deltaZ = movePerTeleport.z;

        Parachute.LOGGER.info("player: {} {} {}", playerPos.x, playerPos.y, playerPos.z);

        var movePacket = new PlayerMoveC2SPacket.Full(
                playerPos.x + deltaX,
                playerPos.y + deltaY,
                playerPos.z + deltaZ,
                90, 90, true);

        packets.add(movePacket);
        Parachute.LOGGER.info("sent, doing {} more", teleportCount - teleportsLeft - 1);
        Parachute.LOGGER.info("teleportsLeft: {}", teleportsLeft);

        playerPos = playerPos.add(deltaX, deltaY, deltaZ);
        teleportsLeft = teleportsLeft - 1;
    }

    public static void tick() {
        if (warping) {
            Parachute.LOGGER.info("TICK");
            var mc = MinecraftClient.getInstance();
            var player = mc.player;

            var connection = (IMixinClientConnection) player.networkHandler.getConnection();

            if (!fuck) {
                switch (stage) {
                    case ATTACK -> {
                        Parachute.LOGGER.info("merking sheep");
                        Parachute.LOGGER.info("{} {} {}", playerPos.x, playerPos.y, playerPos.z);
                        packets.add(PlayerInteractEntityC2SPacket.attack(sheep, player.isSneaking()));
                        Parachute.LOGGER.info("SWITCHING: RETURN");
                        stage = Stage.RETURN;

                        return; /*
                        Parachute.LOGGER.info("TRYING THE FUNNY");
                        var movePacket = new PlayerMoveC2SPacket.Full(
                                origPos.x,
                                origPos.y,
                                origPos.z,
                                90, 90, true);
                        connection.invokeSendImmediately(movePacket, null);

                        warping = false;
                        fuck = true;
                        return; */
                    }
                    case TOWARDS -> {
                        playerPos = origPos;
                        var distance = playerPos.distanceTo(sheepPos);
                        teleportCount = Math.ceil(distance / 10);
                        teleportsLeft = (int) teleportCount;

                        var deltaPos = sheepPos.subtract(playerPos);
                        movePerTeleport = deltaPos.multiply(1 / teleportCount);

                        Parachute.LOGGER.info("delta: {} {} {}", deltaPos.x, deltaPos.y, deltaPos.z);
                    }
                    case RETURN -> {
                        var distance = playerPos.distanceTo(origPos);
                        teleportCount = Math.ceil(distance / 10);
                        teleportsLeft = (int) teleportCount;

                        var deltaPos = origPos.subtract(playerPos);
                        movePerTeleport = deltaPos.multiply(1 / teleportCount);

                        Parachute.LOGGER.info("delta: {} {} {}", deltaPos.x, deltaPos.y, deltaPos.z);
                    }
                }

                fuck = true;
            }

            Parachute.LOGGER.info("stage: {}", stage);

            if (teleportsLeft <= -1) return;

            Parachute.LOGGER.info("WARPING");
            warp();

            if (teleportsLeft <= 0 && stage == Stage.TOWARDS) {
                Parachute.LOGGER.info("SWITCHING: ATTACK");
                stage = Stage.ATTACK;
                fuck = false;
                return;
            }

            if (teleportsLeft <= -1 && stage == Stage.RETURN) {
                Parachute.LOGGER.info("SWITCHING: DONE");

                var movePacket = new PlayerMoveC2SPacket.Full(
                        origPos.x,
                        origPos.y,
                        origPos.z,
                        90, 90, true);
                packets.add(movePacket);

                warping = false;
                fuck = true;
            }
        }
    }

    public static void execute() {
        if (warping) {
            Parachute.LOGGER.info("Cannot run twice");
            return;
        }

        var mc = MinecraftClient.getInstance();
        var player = mc.player;

        var baseX = 2105584;
        var baseY = -35;
        var baseZ = 2105296;
        var entityType = SheepEntity.class;

        //var baseX = -48;
        //var baseY = 62;
        //var baseZ = -5;
        //var entityType = PlayerEntity.class;

        var coords = new BlockPos(baseX - 10, baseY - 10, baseZ - 10);
        var coords2 = new BlockPos(baseX + 10, baseY + 10, baseZ + 10);
        var box = new Box(coords, coords2);

        var sheeps = mc.world.getEntitiesByClass(entityType, box, (entity) -> {
            //return entity != player;
            return true;
        });

        if (sheeps.size() > 0) {
            sheep = sheeps.get(0);

            sheepPos = sheep.getPos();
            origPos = player.getPos();
            Parachute.LOGGER.info("sheep pos: {} {} {}", sheepPos.getX(), sheepPos.getY(), sheepPos.getZ());
            Parachute.LOGGER.info("orig pos: {} {} {}", origPos.getX(), origPos.getY(), origPos.getZ());

            stage = Stage.TOWARDS;
            warping = true;
            fuck = false;

            while (warping == true) {
                tick();
            }

            var connection = (IMixinClientConnection) player.networkHandler.getConnection();
            for (var packet : packets) {
                connection.invokeSendImmediately(packet, null);
            }
        } else {
            Parachute.LOGGER.info("no sheep found");
        }
    }
}
