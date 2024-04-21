package themixray.repeating.mod.mixin;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.Main;

@Mixin(KeyboardInput.class)
public abstract class InputMixin {
	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(boolean slowDown, float f, CallbackInfo ci) {
		if (Main.me.is_replaying) {
			if (Main.input_replay != null) {
				Main.input_replay.inputCallback();
			}
		}
	}
}
