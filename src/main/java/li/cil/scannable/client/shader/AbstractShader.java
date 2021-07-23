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
        Minecraft.getInstance().submitAsync(() -> reloadShaders(resourceManager));
        if (resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).registerReloadListener((ISelectiveResourceReloadListener) (manager, predicate) -> {
                if (predicate.test(VanillaResourceType.SHADERS)) {
                    reloadShaders(manager);
                }
            });
        }
    }

    public void bind() {
        if (shaderInstance != null) {
            timeUniform.set((System.currentTimeMillis() - START_TIME) / 1000.0f);
            shaderInstance.apply();
        }
    }

    public void unbind() {
        if (shaderInstance != null) {
            shaderInstance.clear();
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
        timeUniform = shaderInstance.safeGetUniform("time");
    }
}
