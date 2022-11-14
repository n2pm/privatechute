package pm.n2.parachute.impulses;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import pm.n2.parachute.render.OverlaySheepRenderer;

public class ReachAttack {
    private static boolean isAttacking = false;

    private static final int maxTeleportDistance = 9;
    private static final int maxVanillaAttackDistance = 4;

    public static void attack(Entity target) {
        if (isAttacking) {
            // Cannot attack more than one entity at a time
            // TODO: Attack queue?? if we do tp aura
            return;
        }
        isAttacking = true;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        assert player != null;
        assert world != null;
        ClientConnection connection = player.networkHandler.getConnection();

        Vec3d targetPos = target.getPos();
        // if a short mob is up against ceiling of block
        if (!world.isAir(new BlockPos(targetPos.add(0, 1, 0))) &&
                world.isAir(new BlockPos(targetPos.subtract(0, 1, 0)))) {
            targetPos = targetPos.subtract(0, 1, 0);
        }
        Vec3d origPos = player.getPos();
        Vec3d currentPos = origPos;

        // Go to target
        OverlaySheepRenderer.approachPositions.clear();
        OverlaySheepRenderer.returnPositions.clear();
        double distancePerTeleport = (origPos.distanceTo(targetPos) - maxVanillaAttackDistance) / maxTeleportDistance;
        Vec3d delta = targetPos.subtract(origPos).multiply(1 / distancePerTeleport);
        for (int i = 0; i < Math.floor(distancePerTeleport); i++) {
            currentPos = currentPos.add(delta);
            OverlaySheepRenderer.approachPositions.add(currentPos);
            connection.send(new PlayerMoveC2SPacket.PositionAndOnGround(currentPos.x, currentPos.y, currentPos.z, true));
        }

        // Attack
        connection.send(PlayerInteractEntityC2SPacket.attack(target, false));

        // Return from target
        distancePerTeleport = currentPos.distanceTo(origPos) / maxTeleportDistance;
        delta = origPos.subtract(currentPos).multiply(1 / distancePerTeleport);
        for (int i = 0; i < Math.floor(distancePerTeleport); i++) {
            currentPos = currentPos.add(delta);
            OverlaySheepRenderer.returnPositions.add(currentPos);
            connection.send(new PlayerMoveC2SPacket.PositionAndOnGround(currentPos.x, currentPos.y, currentPos.z, true));
        }

        isAttacking = false;
    }
}
