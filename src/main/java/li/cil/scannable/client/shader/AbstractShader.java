package li.cil.scannable.client.shader;

import li.cil.scannable.common.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderInstance;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.io.IOException;

public abstract class AbstractShader {
    private static final long START_TIME = System.currentTimeMillis();

    protected ShaderInstance shaderInstance;
    protected ShaderDefault timeUniform;

    public void initialize() {
        final IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Minecraft.getInstance().deferTask(() -> reloadShaders(resourceManager));
        if (resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).addReloadListener((ISelectiveResourceReloadListener) (manager, predicate) -> {
                if (predicate.test(VanillaResourceType.SHADERS)) {
                    reloadShaders(manager);
                }
            });
        }
    }

    public void bind() {
        if (shaderInstance != null) {
            timeUniform.set((System.currentTimeMillis() - START_TIME) / 1000.0f);
            shaderInstance.func_216535_f();
        }
    }

    public void unbind() {
        if (shaderInstance != null) {
            shaderInstance.func_216544_e();
        }
    }

    private void reloadShaders(final IResourceManager manager) {
        if (shaderInstance != null) {
            shaderInstance.close();
            shaderInstance = null;
        }

        try {
            shaderInstance = new ShaderInstance(manager, getShaderName());
            handleShaderLoad();
        } catch (final IOException e) {
            Scannable.getLog().error(e);
        }
    }

    protected abstract String getShaderName();

    protected void handleShaderLoad() {
        timeUniform = shaderInstance.getShaderUniform("Time");
    }
}
