package themixray.repeating.mod.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.util.function.BooleanSupplier;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class NetworkMixin {
    @Inject(at = @At(value = "HEAD"), method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V")
    private void onSendPacket1Head(Packet<?> packet,
                                   CallbackInfo ci) {

    }

    @Inject(at = @At(value = "HEAD"), method = "sendPacket(Lnet/minecraft/network/packet/Packet;Ljava/util/function/BooleanSupplier;Ljava/time/Duration;)V")
    private void onSendPacket2Head(Packet<ServerPlayPacketListener> packet,
                                   BooleanSupplier sendCondition,
                                   Duration expirationTime,
                                   CallbackInfo ci) {

    }
}
