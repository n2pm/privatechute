package pm.n2.parachute.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import pm.n2.parachute.impulses.Sheep;

public class SheepCommand {
    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        var sheepCommand = ClientCommandManager.literal("sheep")
                .executes((context) -> {
                    Sheep.execute();
                    return 1;
                });

        var clearCommand = ClientCommandManager.literal("sheepclear")
                .executes((context) -> {
                    Sheep.clear();
                    return 1;
                });

        dispatcher.register(sheepCommand);
        dispatcher.register(clearCommand);
    }
}
