package li.cil.scannable.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.cil.scannable.api.API;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public final class Shaders {
    private static final List<ShaderReference> SHADERS = new ArrayList<>();

    public static ShaderInstance scanEffectShader;
    public static ShaderInstance scanResultShader;

    public static void initialize() {
        addShader("scan_effect", DefaultVertexFormat.POSITION_TEX, shader -> scanEffectShader = shader);
        addShader("scan_result", DefaultVertexFormat.POSITION_TEX_COLOR, shader -> scanResultShader = shader);

        loadAndListenToReload();
    }

    @Nullable
    public static ShaderInstance getScanEffectShader() {
        return scanEffectShader;
    }

    @Nullable
    public static ShaderInstance getScanResultShader() {
        return scanResultShader;
    }

    private static void loadAndListenToReload() {
        final ResourceManagerHelper manager = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        manager.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(API.MOD_ID, "resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                reloadShaders(resourceManager);
            }
        });
    }

    private static void reloadShaders(final ResourceProvider provider) {
        RenderSystem.assertOnRenderThread();
        SHADERS.forEach(reference -> reference.reload(provider));
    }

    private static void addShader(final String name, final VertexFormat format, final Consumer<ShaderInstance> reloadAction) {
        SHADERS.add(new ShaderReference(name, format, reloadAction));
    }

    private static final class ShaderReference {
        private static final Logger LOGGER = LogManager.getLogger();

        private final String name;
        private final VertexFormat format;
        private final Consumer<ShaderInstance> reloadAction;
        private ShaderInstance shader;

        public ShaderReference(final String name, final VertexFormat format, final Consumer<ShaderInstance> reloadAction) {
            this.name = name;
            this.format = format;
            this.reloadAction = reloadAction;
        }

        public void reload(final ResourceProvider provider) {
            if (shader != null) {
                shader.close();
                shader = null;
            }

            try {
                shader = new ShaderInstance(location -> {
                    try {
                        return provider.getResource(new ResourceLocation(API.MOD_ID, location.getPath()));
                    } catch (final IOException e) {
                        return provider.getResource(location);
                    }
                }, name, format);
            } catch (final Exception e) {
                LOGGER.error(e);
            }

            reloadAction.accept(shader);
        }
    }

    private Shaders() {
    }
}
