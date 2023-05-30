package themixray.repeating.mod.mixin;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;

@Mixin(KeyboardInput.class)
public abstract class InputMixin {
	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(boolean slowDown, float f, CallbackInfo ci) {
		if (RepeatingMod.me.is_replaying) {
			if (RepeatingMod.input_replay != null) {
				RepeatingMod.input_replay.inputCallback();
			}
		}
	}
}
