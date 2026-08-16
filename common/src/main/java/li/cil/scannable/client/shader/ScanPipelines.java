package li.cil.scannable.client.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.cil.scannable.api.API;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public final class ScanPipelines {
    // See scan_effect.fsh.
    public static final String SCAN_EFFECT_UNIFORM = "ScanEffect";
    public static final String SCAN_EFFECT_DEPTH_SAMPLER = "depthTexSampler";

    // See scan_result.fsh.
    public static final String SCAN_RESULT_UNIFORM = "ScanResult";

    // Matches vanilla.
    private static final String DYNAMIC_TRANSFORMS_UNIFORM = "DynamicTransforms";
    private static final String PROJECTION_UNIFORM = "Projection";

    // --------------------------------------------------------------------- //

    public static final RenderPipeline SCAN_EFFECT = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
        .withLocation(id("pipeline/scan_effect"))
        .withVertexShader(id("core/scan_effect"))
        .withFragmentShader(id("core/scan_effect"))
        .withSampler(SCAN_EFFECT_DEPTH_SAMPLER)
        .withUniform(SCAN_EFFECT_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
        .withBlend(BlendFunction.ADDITIVE)
        .withCull(false)
        .build();

    public static final RenderPipeline SCAN_RESULT = RenderPipeline.builder()
        .withLocation(id("pipeline/scan_result"))
        .withVertexShader(id("core/scan_result"))
        .withFragmentShader(id("core/scan_result"))
        .withUniform(DYNAMIC_TRANSFORMS_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withUniform(PROJECTION_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withUniform(SCAN_RESULT_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
        .withBlend(BlendFunction.ADDITIVE)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .withCull(false)
        .build();

    public static final RenderPipeline SCAN_RESULT_OVERLAY = RenderPipeline.builder()
        .withLocation(id("pipeline/scan_result_overlay"))
        .withVertexShader("core/position_color")
        .withFragmentShader("core/position_color")
        .withUniform(DYNAMIC_TRANSFORMS_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withUniform(PROJECTION_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .build();

    public static final RenderPipeline SCAN_RESULT_OVERLAY_TEXTURED = RenderPipeline.builder()
        .withLocation(id("pipeline/scan_result_overlay_textured"))
        .withVertexShader("core/position_tex_color")
        .withFragmentShader("core/position_tex_color")
        .withUniform(DYNAMIC_TRANSFORMS_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withUniform(PROJECTION_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withSampler("Sampler0")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .build();

    public static final RenderPipeline SCANNER_PROGRESS = RenderPipeline.builder()
        .withLocation(id("pipeline/scanner_progress"))
        .withVertexShader("core/position_tex_color")
        .withFragmentShader("core/position_tex_color")
        .withUniform(DYNAMIC_TRANSFORMS_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withUniform(PROJECTION_UNIFORM, UniformType.UNIFORM_BUFFER)
        .withSampler("Sampler0")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .withCull(false)
        .build();

    // --------------------------------------------------------------------- //

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(API.MOD_ID, path);
    }

    private ScanPipelines() {
    }
}
