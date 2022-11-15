package li.cil.scannable.client.renderer.forge;

import com.mojang.blaze3d.pipeline.RenderTarget;
import li.cil.scannable.client.renderer.ScannerRenderer;

public final class ScannerRendererImpl {
    public static ScannerRenderer.DepthOnlyRenderTarget copyBufferSettings(final RenderTarget mainRenderTarget, final ScannerRenderer.DepthOnlyRenderTarget depthRenderTarget) {
        if (mainRenderTarget.isStencilEnabled()) {
            depthRenderTarget.enableStencil();
            return depthRenderTarget;
        } else if (depthRenderTarget.isStencilEnabled()) {
            depthRenderTarget.destroyBuffers();
            return new ScannerRenderer.DepthOnlyRenderTarget(depthRenderTarget.width, depthRenderTarget.height);
        } else {
            return depthRenderTarget;
        }
    }
}
