package pm.n2.parachute.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;

public class GMSCommand {
    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<CauldronClientCommandSource> command = ClientCommandManager.literal("gms")
               .executes((context) -> execute(context.getSource(), false));
        dispatcher.register(command);
    }

    public static int execute(CauldronClientCommandSource source, boolean showAll) {
        // New arraylist because for some reason .toList makes an immutable collection
        source.getClient().interactionManager.setGameMode(GameMode.SURVIVAL);
        return 1;
    }
}