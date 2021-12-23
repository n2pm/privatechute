package pm.n2.parachute.events;

import pm.n2.parachute.ParachuteCommands;
import pm.n2.parachute.config.GenericConfigs;
import pm.n2.parachute.gui.ConfigGui;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.minecraft.client.MinecraftClient;
import pm.n2.parachute.mixin.IMixinMinecraftClient;

public class KeyCallbacks {
    private MinecraftClient client;

    public static void init(MinecraftClient client) {
        IHotkeyCallback callbackGeneric = new KeyCallbackHotkeysGeneric(client);

        GenericConfigs.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        GenericConfigs.OPEN_CLIENT_COMMANDS.getKeybind().setCallback(callbackGeneric);
    }

    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback {
        private static MinecraftClient client;
        public KeyCallbackHotkeysGeneric(MinecraftClient client2) {
            client = client2;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            if (key == GenericConfigs.OPEN_CONFIG_GUI.getKeybind()) {
                GuiBase.openGui(new ConfigGui());
                return true;
            }
            if (key == GenericConfigs.OPEN_CLIENT_COMMANDS.getKeybind()) {
                ((IMixinMinecraftClient) client).pubOpenChatScreen(Character.toString(ParachuteCommands.COMMAND_PREFIX));
                return true;
            }
            return false;
        }
    }
}