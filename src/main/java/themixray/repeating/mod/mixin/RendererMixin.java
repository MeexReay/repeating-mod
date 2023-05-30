package themixray.repeating.mod.mixin;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;

@Mixin(GameRenderer.class)
public abstract class RendererMixin {
	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(CallbackInfo ci) {
		if (RepeatingMod.me.is_replaying) {
			if (RepeatingMod.input_replay != null) {
				RepeatingMod.me.recordCameraInput();
			}
		}
	}
}
