package net.riiael.mixin.client.keymapping;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow
    private boolean isDown;

    private static final long DEBOUNCE_MS = 15L;
    private long releaseTime = 0;
    private boolean fakingDown = false;

    @Inject(method = "setDown", at = @At("HEAD"))
    private void onSetDown(boolean bl, CallbackInfo ci) {
        KeyMapping keyMapping = (KeyMapping) (Object) this;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options == null || keyMapping != minecraft.options.keyShift) return;

        if (!bl && this.isDown) {
            releaseTime = System.currentTimeMillis();
            fakingDown = true;
        } else if (bl) {
            fakingDown = false;
        }
    }
    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
        KeyMapping keyMapping = (KeyMapping) (Object) this;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options != null && keyMapping == minecraft.options.keyShift) {
            if (fakingDown) {
                if (System.currentTimeMillis() - releaseTime < DEBOUNCE_MS) {
                    cir.setReturnValue(true);
                } else {
                    fakingDown = false;
                }
            }
        }
    }
}

