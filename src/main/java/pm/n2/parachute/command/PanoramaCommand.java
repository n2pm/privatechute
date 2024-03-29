package pm.n2.parachute.command;

import com.adryd.cauldron.api.command.CauldronClientCommandSource;
import com.adryd.cauldron.api.command.ClientCommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import pm.n2.parachute.Parachute;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

/**
 * @author NotNite <hi@notnite.com>
 */
public class PanoramaCommand {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    // stolen from ScreenshotRecorder
    private static File getPanoramaFilename(File directory) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;

        while (true) {
            File file = new File(directory, "panorama_" + string + (i == 1 ? "" : "_" + i) + "/");
            if (!file.exists()) {
                return file;
            }

            ++i;
        }
    }

    public static int run(CommandContext<CauldronClientCommandSource> ctx, int size, boolean shouldAlign) {
        // client.takePanorama takes an argument and a File
        // from that file, it makes the "screenshots" folder INSIDE of it
        // i don't want to write a mixin or paste-rewrite this function,
        // so i make the folder and just move it up
        // it's messy but i don't care
        try {
            ClientPlayerEntity player = ctx.getSource().getPlayer();
            float oldYaw = player.getYaw();
            float oldPitch = player.getPitch();
            if (shouldAlign) {
                player.setYaw(0.0F);
                player.setPitch(0.0F);
            }

            // make sure screenshots exist lol
            MinecraftClient client = ctx.getSource().getClient();
            File screenshotDir = new File(client.runDirectory, "screenshots");
            if (!screenshotDir.exists()) screenshotDir.mkdir();

            // I HATE MINECRAFT
            File panoramaFolder = getPanoramaFilename(screenshotDir);
            File panoramaScreenshotsDir = new File(panoramaFolder, "screenshots");
            Files.createDirectories(panoramaScreenshotsDir.toPath());

            Text result = client.takePanorama(panoramaFolder, size, size);
            // move the files up
            // yes IDEA made this Objects.requireNonNull
            for (File file : Objects.requireNonNull(panoramaScreenshotsDir.listFiles())) {
                file.renameTo(new File(panoramaFolder, file.getName()));
            }
            panoramaScreenshotsDir.delete();

            if (shouldAlign) {
                player.setYaw(oldYaw);
                player.setPitch(oldPitch);
            }

            ctx.getSource().sendFeedback(result);
            return 1;
        } catch (IOException err) {
            Parachute.LOGGER.error(err);
            ctx.getSource().sendFeedback(Text.translatable("screenshot.failure", err));
            return 0;
        }
    }

    public static void register(CommandDispatcher<CauldronClientCommandSource> dispatcher) {
        // fuck optional arguments
        LiteralArgumentBuilder<CauldronClientCommandSource> command = ClientCommandManager
                .literal("panorama")
                .then(ClientCommandManager.argument("size", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("align", BoolArgumentType.bool())
                                .executes(ctx -> run(ctx, getInteger(ctx, "size"), getBool(ctx, "align"))))
                        .executes(ctx -> run(ctx, getInteger(ctx, "size"), false)))
                .executes(ctx -> run(ctx, 1024, false));
        dispatcher.register(command);
    }
}
