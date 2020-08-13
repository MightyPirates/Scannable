package li.cil.scannable.client.shader;

import li.cil.scannable.api.API;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.shader.ShaderDefault;

public final class ScanResultShader extends AbstractShader {
    public static ScanResultShader INSTANCE = new ScanResultShader();

    private static ShaderDefault projMatUniform;

    public static void setProjectionMatrix(final Matrix4f matrix) {
        projMatUniform.set(matrix);
    }

    @Override
    protected String getShaderName() {
        return API.MOD_ID + ":scan_result";
    }

    @Override
    protected void handleShaderLoad() {
        super.handleShaderLoad();
        projMatUniform = shaderInstance.getShaderUniform("ProjMat");
    }
}
