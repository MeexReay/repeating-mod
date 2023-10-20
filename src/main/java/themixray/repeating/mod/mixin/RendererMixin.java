package themixray.repeating.mod.mixin;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;
import themixray.repeating.mod.TickTask;

@Mixin(GameRenderer.class)
public abstract class RendererMixin {
	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void onTickHead(CallbackInfo ci) {
		TickTask.tickTasks(TickTask.TickAt.RENDER_HEAD);
	}

	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(CallbackInfo ci) {
		TickTask.tickTasks(TickTask.TickAt.RENDER_TAIL);
	}
}
