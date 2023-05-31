package themixray.repeating.mod.mixin;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.RepeatingMod;
import themixray.repeating.mod.TickTask;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow public abstract UUID getUuid();

	@Inject(at = @At(value = "HEAD"), method = "setSprinting", cancellable = true)
	private void onSprint(boolean sprinting,CallbackInfo ci) {
		if (getUuid().equals(RepeatingMod.client.player.getUuid())) {
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
}
