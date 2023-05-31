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

import java.util.ArrayList;

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

	@Inject(at = @At(value = "HEAD"), method = "tickMovement")
	private void onMoveHead(CallbackInfo ci) {
		TickTask.tickTasks(TickTask.TickAt.MOVEMENT_HEAD);
	}

	@Inject(at = @At(value = "TAIL"), method = "tick")
	private void onMoveTail(CallbackInfo ci) {
		TickTask.tickTasks(TickTask.TickAt.MOVEMENT_TAIL);
	}
}
