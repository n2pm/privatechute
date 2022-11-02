package pm.n2.parachute.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketSendListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientConnection.class)
public interface IMixinClientConnection {
    @Invoker("sendImmediately")
    void invokeSendImmediately(Packet<?> packet, @Nullable PacketSendListener listener);
}
