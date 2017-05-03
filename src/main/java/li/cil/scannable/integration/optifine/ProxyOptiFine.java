package li.cil.scannable.integration.optifine;

import li.cil.scannable.common.Scannable;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public enum ProxyOptiFine {
    INSTANCE;

    private IntSupplier getDepthTexture;
    private BooleanSupplier isShaderPackLoaded;

    ProxyOptiFine() {
        try {
            final Class<?> clazz = Class.forName("shadersmod.client.Shaders");

            final Field depthTexturesField = clazz.getDeclaredField("dfbDepthTextures");
            depthTexturesField.setAccessible(true);
            final IntBuffer depthTextures = (IntBuffer) depthTexturesField.get(null);
            getDepthTexture = () -> depthTextures.get(0);

            final Field field = clazz.getDeclaredField("shaderPackLoaded");
            field.setAccessible(true);
            isShaderPackLoaded = () -> {
                try {
                    return field.getBoolean(null);
                } catch (final IllegalAccessException e) {
                    Scannable.getLog().warn("Failed reading field indicating whether shaders are enabled. Shader mod integration disabled.");
                    getDepthTexture = null;
                    isShaderPackLoaded = null;
                    return false;
                }
            };

            Scannable.getLog().info("Successfully integrated with shader mod.");
        } catch (final ClassNotFoundException e) {
            Scannable.getLog().info("No shader mod found, we'll do our own hacks to inject a depth texture when needed.");
        } catch (final IllegalAccessException | NoSuchFieldException e) {
            Scannable.getLog().warn("Failed integrating with shader mod. Ignoring.");
        }
    }

    public boolean isShaderPackLoaded() {
        return isShaderPackLoaded != null && isShaderPackLoaded.getAsBoolean();
    }

    public int getDepthTexture() {
        return getDepthTexture != null ? getDepthTexture.getAsInt() : 0;
    }
}
