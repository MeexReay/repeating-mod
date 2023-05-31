package themixray.repeating.mod.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;
import themixray.repeating.mod.TickTask;

@Mixin(MinecraftClient.class)
public abstract class ClientMixin {
	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void onTickHead(CallbackInfo ci) {
		if (RepeatingMod.me.is_recording)
			RepeatingMod.me.recordAllInput();
		TickTask.tickTasks(TickTask.TickAt.CLIENT_HEAD);
	}

	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(CallbackInfo ci) {
		TickTask.tickTasks(TickTask.TickAt.CLIENT_TAIL);
	}
}
