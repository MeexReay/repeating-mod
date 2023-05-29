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

	@Inject(at = @At(value = "HEAD"), method = "move")
	private void onMove(MovementType movementType, Vec3d vec, CallbackInfo ci) {
		if (RepeatingMod.me.is_recording) {
			if (vec != lastVec) {
				double dist = 0;
				if (lastVec != null)
					dist = vec.distanceTo(lastVec);
				if (dist > 0.0) {
					Vec3d c = client.player.getPos();

					RepeatingMod.RecordMoveEvent ev = new RepeatingMod.RecordMoveEvent(
							new Vec3d(c.getX() + vec.getX(),
									c.getY() + vec.getY(),
									c.getZ() + vec.getZ()),
							lastYaw, lastPitch);

					boolean just_add = true;
					Date now = new Date();
					if (RepeatingMod.me.last_record != null) {
						long diff = now.getTime() - RepeatingMod.me.last_record.getTime();
						boolean add_delay = true;
						if (diff > 0) {
							RepeatingMod.RecordEvent last_ev = RepeatingMod.me.record.get(RepeatingMod.me.record.size()-1);
							if (last_ev instanceof RepeatingMod.RecordMoveEvent) {
								RepeatingMod.RecordMoveEvent last_ev1 = (RepeatingMod.RecordMoveEvent) last_ev;
								if (last_ev1.vec.distanceTo(ev.vec) < RepeatingMod.me.record_blocks_limit &&
										diff < RepeatingMod.me.record_time_limit) {
									just_add = false;
									add_delay = false;
									last_ev1.vec = ev.vec;
								}
							}
						}
						if (add_delay) {
							RepeatingMod.me.record.add(new RepeatingMod.RecordDelayEvent(diff));
						}
					}
					if (just_add) {
						RepeatingMod.me.record.add(ev);
						RepeatingMod.me.last_record = now;
					}
				}
			}
			lastVec = vec;
		}
	}
}
