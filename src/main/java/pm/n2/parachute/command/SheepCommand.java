package pm.n2.parachute.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import pm.n2.parachute.impulses.ReachAttack;

import java.util.List;

public class SheepCommand {
    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<CauldronClientCommandSource> command = ClientCommandManager.literal("sheep")
                .executes((context) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    assert client.world != null;

                    int baseX = 2105584;
                    int baseY = -35;
                    int baseZ = 2105296;
                    Class<SheepEntity> entityType = SheepEntity.class;

                    BlockPos min = new BlockPos(baseX - 10, baseY - 10, baseZ - 10);
                    BlockPos max = new BlockPos(baseX + 10, baseY + 10, baseZ + 10);
                    Box box = new Box(min, max);

                    List<SheepEntity> sheep = client.world.getEntitiesByClass(entityType, box, (entity) -> true);
                    if (sheep.size() > 0) {
                        ReachAttack.attack(sheep.get(0));
                    }
                    return 1;
                });
        dispatcher.register(command);
    }
}
