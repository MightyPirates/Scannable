package li.cil.scannable.client.shader;

import li.cil.scannable.api.API;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

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

    public void setPosition(final Vector3d value) {
        positionUniform.set((float) value.x(), (float) value.y(), (float) value.z());
    }

    public void setCenter(final Vector3d value) {
        centerUniform.set((float) value.x(), (float) value.y(), (float) value.z());
    }

    public void setRadius(final float value) {
        radiusUniform.set(value);
    }

    public void setDepthBuffer(final int buffer) {
        shaderInstance.setSampler("depthTex", () -> buffer);
    }

    @Override
    protected String getShaderName() {
        return API.MOD_ID + ":scan_effect";
    }

    @Override
    protected void handleShaderLoad() {
        super.handleShaderLoad();
        inverseViewMatrixUniform = shaderInstance.safeGetUniform("invViewMat");
        inverseProjectionMatrixUniform = shaderInstance.safeGetUniform("invProjMat");
        positionUniform = shaderInstance.safeGetUniform("pos");
        centerUniform = shaderInstance.safeGetUniform("center");
        radiusUniform = shaderInstance.safeGetUniform("radius");
    }
}
