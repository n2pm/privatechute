package pm.n2.parachute;

import com.adryd.cauldron.api.render.helper.OverlayRenderManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraft.util.Formatting;
import com.adryd.cauldron.api.command.ClientCommandManager;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import pm.n2.parachute.command.ModsCommand;
import pm.n2.parachute.command.PanoramaCommand;
import pm.n2.parachute.command.SheepCommand;
import pm.n2.parachute.impulses.Sheep;
import pm.n2.parachute.render.OverlayRendererWorldEditCUI;

public class ParachuteClient implements ClientModInitializer {
    private static String MOD_VERSION = "0.0.0";

    public static String getFormattedModVersion() {
        String version = getModVersion();
        Formatting color;

        if (version.contains("+") && version.split("\\+")[1].contains(".")) {
            // dev build
            color = Formatting.RED;
        } else if (version.contains("+")) {
            // ci build
            color = Formatting.LIGHT_PURPLE;
        } else {
            // release build
            color = Formatting.GREEN;
        }

        return color + version + Formatting.RESET;
    }

    public static String getModVersion() {
        return MOD_VERSION;
    }

    public void onInitializeClient(ModContainer mod) {
        MOD_VERSION = mod.metadata()
                .version()
                .raw();

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        Parachute.LOGGER.info("Hello from parachute <3!");

        PanoramaCommand.register(ClientCommandManager.DISPATCHER);
        ModsCommand.register(ClientCommandManager.DISPATCHER);
        SheepCommand.register(ClientCommandManager.DISPATCHER);

        OverlayRenderManager.addRenderer(new OverlayRendererWorldEditCUI());

        ClientTickEvents.START.register((client) -> {
            Sheep.tick();
        });
    }
}