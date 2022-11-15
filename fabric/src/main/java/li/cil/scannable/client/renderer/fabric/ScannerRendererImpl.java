package li.cil.scannable.client.renderer.fabric;

import com.mojang.blaze3d.pipeline.RenderTarget;
import li.cil.scannable.client.renderer.ScannerRenderer;

public final class ScannerRendererImpl {
    public static ScannerRenderer.DepthOnlyRenderTarget copyBufferSettings(final RenderTarget mainRenderTarget, final ScannerRenderer.DepthOnlyRenderTarget depthRenderTarget) {
        return depthRenderTarget;
    }
}
