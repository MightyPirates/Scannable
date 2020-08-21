package li.cil.scannable.client.shader;

import li.cil.scannable.api.API;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.util.math.vector.Matrix4f;

public final class ScanResultShader extends AbstractShader {
    public static ScanResultShader INSTANCE = new ScanResultShader();

    private static ShaderDefault projMatUniform;
    private static ShaderDefault viewMatUniform;

    public static void setProjectionMatrix(final Matrix4f matrix) {
        projMatUniform.set(matrix);
    }

    public static void setViewMatrix(final Matrix4f matrix) {
        viewMatUniform.set(matrix);
    }

    @Override
    protected String getShaderName() {
        return API.MOD_ID + ":scan_result";
    }

    @Override
    protected void handleShaderLoad() {
        super.handleShaderLoad();
        projMatUniform = shaderInstance.getShaderUniform("projMat");
        viewMatUniform = shaderInstance.getShaderUniform("viewMat");
    }
}
