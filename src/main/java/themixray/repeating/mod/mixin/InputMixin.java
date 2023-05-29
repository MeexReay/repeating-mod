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
			RepeatingMod.client.player.input.sneaking = RepeatingMod.replay_sneaking;
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void onTickHead(boolean slowDown, float f, CallbackInfo ci) {
		if (RepeatingMod.me.is_recording) {
			RepeatingMod.RecordSneakEvent e = new RepeatingMod.
				RecordSneakEvent(RepeatingMod.client.player.input.sneaking);
			RepeatingMod.RecordSneakEvent l = ((RepeatingMod.RecordSneakEvent)
					RepeatingMod.me.getLastRecord("sneak"));
			if (l == null || l.sneaking != e.sneaking)
				RepeatingMod.me.recordTick(e);
		}
	}
}
