package li.cil.scannable.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.renderer.OverlayRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private void beforeRenderGui(float f, long l, boolean bl, CallbackInfo ci) {
        ScanManager.onPreRenderGameOverlay(f);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", shift = At.Shift.AFTER))
    private void afterRenderGui(float f, long l, boolean bl, CallbackInfo ci) {
        OverlayRenderer.onOverlayRender(new PoseStack(), f);
    }
}
