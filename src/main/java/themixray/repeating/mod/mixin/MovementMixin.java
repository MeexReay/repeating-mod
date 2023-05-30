package themixray.repeating.mod.mixin;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;

import java.util.Date;

@Mixin(ClientPlayerEntity.class)
public abstract class MovementMixin {
	public Vec3d lastVec = null;
	@Shadow public abstract void sendMessage(Text message);
	@Shadow @Final protected MinecraftClient client;
	@Shadow public abstract float getYaw(float tickDelta);
	@Shadow private float lastYaw;
	@Shadow private float lastPitch;

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
	private void onMove(CallbackInfo ci) {
		if (RepeatingMod.me.is_recording) {
			RepeatingMod.RecordInputEvent l = ((RepeatingMod.RecordInputEvent)RepeatingMod.me.getLastRecord("input"));
			if (l == null) {
				RepeatingMod.RecordInputEvent e = new RepeatingMod.RecordInputEvent(
						RepeatingMod.client.player.input.sneaking,
						RepeatingMod.client.player.input.jumping,
						RepeatingMod.client.player.input.movementSideways,
						RepeatingMod.client.player.input.movementForward,
						RepeatingMod.client.player.input.pressingForward,
						RepeatingMod.client.player.input.pressingBack,
						RepeatingMod.client.player.input.pressingLeft,
						RepeatingMod.client.player.input.pressingRight,
						RepeatingMod.client.player.getHeadYaw(),
						RepeatingMod.client.player.getBodyYaw(),
						RepeatingMod.client.player.getPitch(),
						RepeatingMod.client.player.isSprinting(),
						RepeatingMod.client.player.getYaw());
				RepeatingMod.me.recordTick(e);
			} else {
				RepeatingMod.RecordInputEvent e = new RepeatingMod.RecordInputEvent(
						((Boolean) RepeatingMod.client.player.input.sneaking == l.sneaking) ? null : RepeatingMod.client.player.input.sneaking,
						((Boolean) RepeatingMod.client.player.input.jumping == l.jumping) ? null : RepeatingMod.client.player.input.jumping,
						(((Float) RepeatingMod.client.player.input.movementSideways).equals(l.movementSideways)) ? null : RepeatingMod.client.player.input.movementSideways,
						(((Float) RepeatingMod.client.player.input.movementForward).equals(l.movementForward)) ? null : RepeatingMod.client.player.input.movementForward,
						((Boolean) RepeatingMod.client.player.input.pressingForward == l.pressingForward) ? null : RepeatingMod.client.player.input.pressingForward,
						((Boolean) RepeatingMod.client.player.input.pressingBack == l.pressingBack) ? null : RepeatingMod.client.player.input.pressingBack,
						((Boolean) RepeatingMod.client.player.input.pressingLeft == l.pressingLeft) ? null : RepeatingMod.client.player.input.pressingLeft,
						((Boolean) RepeatingMod.client.player.input.pressingRight == l.pressingRight) ? null : RepeatingMod.client.player.input.pressingRight,
						RepeatingMod.client.player.getHeadYaw(),RepeatingMod.client.player.getBodyYaw(),RepeatingMod.client.player.getPitch(),
						((Boolean) RepeatingMod.client.player.isSprinting() == l.sprinting) ? null : RepeatingMod.client.player.isSprinting(),
						RepeatingMod.client.player.getYaw());

				if (!(e.isEmpty() &&
						e.yaw == l.yaw &&
						e.head_yaw == l.head_yaw &&
						e.pitch == l.pitch &&
						e.body_yaw == l.body_yaw)) {
					RepeatingMod.me.recordTick(e);
				}
			}
		}
	}

	@Inject(at = @At(value = "INVOKE"), method = "setSprinting", cancellable = true)
	private void onSprint(boolean sprinting,CallbackInfo ci) {
		if (RepeatingMod.me.is_replaying) {
			if (RepeatingMod.input_replay != null &&
				RepeatingMod.input_replay.sprinting != sprinting) {
				ci.cancel();
			}
		}
	}
}
