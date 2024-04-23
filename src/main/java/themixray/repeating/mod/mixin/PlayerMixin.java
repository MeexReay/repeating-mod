package themixray.repeating.mod.mixin;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themixray.repeating.mod.Main;

import java.util.UUID;

@Mixin(ClientConnection.class)
public abstract class PlayerMixin {
    @Inject(at = @At(value = "HEAD"), method = "disconnect")
    private void disconnect(Text disconnectReason, CallbackInfo ci) {
        System.out.println("on client close");
    }
}
