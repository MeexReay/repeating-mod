package themixray.repeating.mod.mixin;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;
import themixray.repeating.mod.TickTask;

@Mixin(ClientPlayerEntity.class)
public abstract class MovementMixin {

	@Inject(at = @At(value = "HEAD"), method = "init")
	private void init(CallbackInfo ci) {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, blockState, blockEntity) -> {
			if (RepeatingMod.me.is_recording)
				RepeatingMod.me.recordTick(new RepeatingMod.RecordBlockBreakEvent(pos));
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (hitResult.getType().equals(HitResult.Type.BLOCK))
				if (RepeatingMod.me.is_recording)
					RepeatingMod.me.recordTick(new RepeatingMod.RecordBlockInteractEvent(hand,hitResult));
			return ActionResult.PASS;
		});
	}

	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void onTickHead(CallbackInfo ci) {
		for (TickTask t:TickTask.ticks)
			if (t.getAt() == TickTask.TickAt.HEAD)
				t.tick();
	}

	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onTickTail(CallbackInfo ci) {
		for (TickTask t:TickTask.ticks)
			if (t.getAt() == TickTask.TickAt.TAIL)
				t.tick();
	}

	@Inject(at = @At(value = "HEAD"), method = "tickMovement")
	private void onMove(CallbackInfo ci) {
		if (RepeatingMod.me.is_recording) {
			RepeatingMod.me.recordAllInput();
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "setSprinting", cancellable = true)
	private void onSprint(boolean sprinting,CallbackInfo ci) {
		if (RepeatingMod.me.is_replaying) {
			if (RepeatingMod.input_replay != null &&
					RepeatingMod.input_replay.sprinting != null &&
					RepeatingMod.input_replay.sprinting != sprinting) {
				ci.cancel();
				return;
			}
		}
	}
}
