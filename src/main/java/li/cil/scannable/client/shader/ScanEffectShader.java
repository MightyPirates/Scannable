package li.cil.scannable.client.shader;

import li.cil.scannable.api.API;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.util.math.Vec3d;

public final class ScanEffectShader extends AbstractShader {
    public static final ScanEffectShader INSTANCE = new ScanEffectShader();

    private ShaderDefault inverseViewMatrixUniform;
    private ShaderDefault inverseProjectionMatrixUniform;
    private ShaderDefault positionUniform;
    private ShaderDefault centerUniform;
    private ShaderDefault radiusUniform;

    public void setInverseViewMatrix(final Matrix4f value) {
        inverseViewMatrixUniform.set(value);
    }

    public void setInverseProjectionMatrix(final Matrix4f value) {
        inverseProjectionMatrixUniform.set(value);
    }

    public void setPosition(final Vec3d value) {
        positionUniform.set((float) value.getX(), (float) value.getY(), (float) value.getZ());
    }

    public void setCenter(final Vec3d value) {
        centerUniform.set((float) value.getX(), (float) value.getY(), (float) value.getZ());
    }

    public void setRadius(final float value) {
        radiusUniform.set(value);
    }

    public void setDepthBuffer(final int buffer) {
        shaderInstance.func_216537_a("depthTex", buffer);
    }

    @Override
    protected String getShaderName() {
        return API.MOD_ID + ":scan_effect";
    }

    @Override
    protected void handleShaderLoad() {
        super.handleShaderLoad();
        inverseViewMatrixUniform = shaderInstance.getShaderUniform("invViewMat");
        inverseProjectionMatrixUniform = shaderInstance.getShaderUniform("invProjMat");
        positionUniform = shaderInstance.getShaderUniform("pos");
        centerUniform = shaderInstance.getShaderUniform("center");
        radiusUniform = shaderInstance.getShaderUniform("radius");
    }
}
