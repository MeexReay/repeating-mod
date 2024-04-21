package themixray.repeating.mod.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.Main;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow public abstract UUID getUuid();

	@Inject(at = @At(value = "HEAD"), method = "setSprinting", cancellable = true)
	private void onSprint(boolean sprinting,CallbackInfo ci) {
		if (getUuid().equals(Main.client.player.getUuid())) {
			if (Main.me.is_replaying) {
				if (Main.input_replay != null &&
						Main.input_replay.sprinting != null &&
						Main.input_replay.sprinting != sprinting) {
					ci.cancel();
					return;
				}
			}
		}
	}
}
