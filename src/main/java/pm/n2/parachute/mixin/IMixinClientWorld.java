package pm.n2.parachute.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.unmapped.C_czisrdmd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientWorld.class)
public interface IMixinClientWorld {
    @Invoker
    C_czisrdmd invokeM_lvsrwztn();
}
