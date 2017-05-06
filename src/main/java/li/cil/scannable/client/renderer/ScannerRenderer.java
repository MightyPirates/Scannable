package li.cil.scannable.client.renderer;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.integration.optifine.ProxyOptiFine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

@SideOnly(Side.CLIENT)
public enum ScannerRenderer {
    INSTANCE;

    // --------------------------------------------------------------------- //
    // Settings

    // Locations of the actual shaders used for rendering the scanner effect.
    private static final ResourceLocation VERTEX_SHADER_LOCATION = new ResourceLocation(API.MOD_ID, "shaders/scanner.vsh");
    private static final ResourceLocation FRAGMENT_SHADER_LOCATION = new ResourceLocation(API.MOD_ID, "shaders/scanner.fsh");

    // --------------------------------------------------------------------- //
    // Resolved locations of uniforms in the shader, cached for speed.

    private int vertexShader, fragmentShader, shaderProgram;
    private int camPosUniform, centerUniform, radiusUniform;
    private int zNearUniform, zFarUniform, aspectUniform;
    private int depthTexUniform;
    private int framebufferDepthTexture;
    private boolean isOptiFineDepthTexture;

    // --------------------------------------------------------------------- //
    // Direct memory float buffers for setting uniforms, cached for alloc-free.

    private final FloatBuffer float1Buffer = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer float3Buffer = BufferUtils.createFloatBuffer(3);
    private final FloatBuffer float16Buffer = BufferUtils.createFloatBuffer(16);

    // --------------------------------------------------------------------- //
    // Matrices and corner ray vertices for alloc-free.

    private final Matrix4f projectionMatrix = new Matrix4f(), modelViewMatrix = new Matrix4f(), mvpMatrix = new Matrix4f();
    private final Vector4f tempCorner = new Vector4f();
    private final Vector3f topLeft = new Vector3f(), topRight = new Vector3f(), bottomLeft = new Vector3f(), bottomRight = new Vector3f();

    // View space coordinates of screen corners.
    public static final Vector4f CORNER_TOP_LEFT = new Vector4f(-1f, 1f, 1f, 1f);
    public static final Vector4f CORNER_TOP_RIGHT = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector4f CORNER_BOTTOM_LEFT = new Vector4f(-1f, -1f, 1f, 1f);
    public static final Vector4f CORNER_BOTTOM_RIGHT = new Vector4f(1f, -1f, 1f, 1f);

    // --------------------------------------------------------------------- //
    // State of the scanner, set when triggering a ping.

    private long currentStart = -1;

    // --------------------------------------------------------------------- //

    /**
     * Initialize or re-initialize the shader used for the scanning effect.
     */
    public void init() {
        try {
            deleteShader();
            vertexShader = loadShader(GL20.GL_VERTEX_SHADER, VERTEX_SHADER_LOCATION);
            fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_LOCATION);
            shaderProgram = linkProgram(vertexShader, fragmentShader);
            camPosUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "camPos");
            centerUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "center");
            radiusUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "radius");
            zNearUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "zNear");
            zFarUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "zFar");
            aspectUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "aspect");
            depthTexUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "depthTex");
        } catch (final Exception e) {
            deleteShader();
            Scannable.getLog().error("Failed loading shader.", e);
        }
    }

    public void ping(final Vec3d pos) {
        if (shaderProgram == 0) {
            return;
        }

        if (!OpenGlHelper.isFramebufferEnabled()) {
            return;
        }

        currentStart = System.currentTimeMillis();

        OpenGlHelper.glUseProgram(shaderProgram);

        final Minecraft mc = Minecraft.getMinecraft();
        final Framebuffer framebuffer = mc.getFramebuffer();

        setUniform(aspectUniform, framebuffer.framebufferTextureWidth / (float) framebuffer.framebufferTextureHeight);
        setUniform(zNearUniform, 0.05f);
        setUniform(zFarUniform, mc.gameSettings.renderDistanceChunks * 16);
        setUniform(centerUniform, pos);

        OpenGlHelper.glUseProgram(0);
    }

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (currentStart < 0) {
            return;
        }

        if (shaderProgram == 0) {
            return;
        }

        if (!OpenGlHelper.isFramebufferEnabled()) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        final World world = mc.world;
        if (world == null) {
            return;
        }

        final Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) {
            return;
        }

        checkError("Pre rendering");

        final Framebuffer framebuffer = mc.getFramebuffer();
        final int adjustedDuration = ScanManager.computeScanGrowthDuration();

        if (framebufferDepthTexture == 0) {
            if (adjustedDuration > (int) (System.currentTimeMillis() - currentStart)) {
                installDepthTexture(framebuffer);
            } else {
                return;
            }
        } else {
            if (adjustedDuration < (int) (System.currentTimeMillis() - currentStart)) {
                uninstallDepthTexture(framebuffer);
                currentStart = -1; // for early exit
                return;
            }
        }

        setupCorners();

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        if (!isOptiFineDepthTexture) {
            // Activate original depth render buffer while we use the depth texture.
            // Even though it's not written to typically drivers won't like reading
            // from a sampler of a texture that's part of the current render target.
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
            checkError("Swap in depth buffer");
        }

        final int width = framebuffer.framebufferTextureWidth;
        final int height = framebuffer.framebufferTextureHeight;
        final float radius = ScanManager.computeRadius(currentStart, adjustedDuration);
        GlStateManager.bindTexture(framebufferDepthTexture);

        final int oldProgram = GlStateManager.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        OpenGlHelper.glUseProgram(shaderProgram);

        setUniform(camPosUniform, viewer.getPositionEyes(event.getPartialTicks()));
        setUniform(radiusUniform, radius);
        OpenGlHelper.glUniform1i(depthTexUniform, 0);

        setupMatrices(width, height);

        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();

        // Use the normal to pass along the ray direction for each corner.
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        buffer.pos(0, height, 0).tex(0, 0).normal(bottomLeft.x, bottomLeft.y, bottomLeft.z).endVertex();
        buffer.pos(width, height, 0).tex(1, 0).normal(bottomRight.x, bottomRight.y, bottomRight.z).endVertex();
        buffer.pos(width, 0, 0).tex(1, 1).normal(topRight.x, topRight.y, topRight.z).endVertex();
        buffer.pos(0, 0, 0).tex(0, 1).normal(topLeft.x, topLeft.y, topLeft.z).endVertex();

        tessellator.draw();

        restoreMatrices();

        OpenGlHelper.glUseProgram(oldProgram);

        GlStateManager.bindTexture(0);

        if (!isOptiFineDepthTexture) {
            // Swap back in our depth texture for that sweet, sweet depth info.
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);
            checkError("Swap out depth buffer");
        }

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        checkError("Post rendering");
    }

    // --------------------------------------------------------------------- //

    private static int loadShader(final int type, final ResourceLocation location) throws Exception {
        final int shader = OpenGlHelper.glCreateShader(type);
        compileShader(shader, location);
        return shader;
    }

    private static void compileShader(final int shader, final ResourceLocation location) throws Exception {
        final IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);

        try (final InputStream stream = resource.getInputStream()) {
            final byte[] bytes = IOUtils.toByteArray(stream);
            final ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.rewind();
            OpenGlHelper.glShaderSource(shader, buffer);
        }

        OpenGlHelper.glCompileShader(shader);
        if (OpenGlHelper.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new Exception(OpenGlHelper.glGetShaderInfoLog(shader, 4096));
        }
    }

    private static int linkProgram(final int vertexShader, final int fragmentShader) throws Exception {
        final int program = OpenGlHelper.glCreateProgram();
        OpenGlHelper.glAttachShader(program, vertexShader);
        OpenGlHelper.glAttachShader(program, fragmentShader);
        OpenGlHelper.glLinkProgram(program);
        if (OpenGlHelper.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new Exception(OpenGlHelper.glGetProgramInfoLog(program, 4096));
        }
        return program;
    }

    private void deleteShader() {
        if (shaderProgram != 0) {
            OpenGlHelper.glDeleteProgram(shaderProgram);
            shaderProgram = 0;
        }
        if (vertexShader != 0) {
            OpenGlHelper.glDeleteShader(vertexShader);
            vertexShader = 0;
        }
        if (fragmentShader != 0) {
            OpenGlHelper.glDeleteShader(fragmentShader);
            fragmentShader = 0;
        }
    }

    private static void checkError(final String context) {
        final int error = GL11.glGetError();
        if (error != 0) {
            final String errorMessage = GLU.gluErrorString(error);
            Scannable.getLog().warn("[OpenGL Error: {}] {}: {}", error, context, errorMessage);
        }
    }

    private void installDepthTexture(final Framebuffer framebuffer) {
        if (ProxyOptiFine.INSTANCE.isShaderPackLoaded()) {
            framebufferDepthTexture = ProxyOptiFine.INSTANCE.getDepthTexture();
            isOptiFineDepthTexture = true;
            return;
        } else {
            isOptiFineDepthTexture = false;
        }

        framebufferDepthTexture = TextureUtil.glGenTextures();
        GlStateManager.bindTexture(framebufferDepthTexture);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
//        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
        GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_BYTE, null);
        GlStateManager.bindTexture(0);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebuffer.framebufferObject);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);

        checkError("Install Depth Texture");
    }

    private void uninstallDepthTexture(final Framebuffer framebuffer) {
        if (isOptiFineDepthTexture) {
            framebufferDepthTexture = 0;
            return;
        }

        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebuffer.framebufferObject);
        OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
        TextureUtil.deleteTexture(framebufferDepthTexture);
        framebufferDepthTexture = 0;

        checkError("Uninstall Depth Texture");
    }

    private void setupCorners() {
        getMatrix(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        getMatrix(GL11.GL_MODELVIEW_MATRIX, modelViewMatrix);
        Matrix4f.mul(projectionMatrix, modelViewMatrix, mvpMatrix);
        mvpMatrix.invert();
        setupCorner(CORNER_TOP_LEFT, topLeft);
        setupCorner(CORNER_TOP_RIGHT, topRight);
        setupCorner(CORNER_BOTTOM_LEFT, bottomLeft);
        setupCorner(CORNER_BOTTOM_RIGHT, bottomRight);
    }

    private void setupCorner(final Vector4f corner, Vector3f into) {
        Matrix4f.transform(mvpMatrix, corner, tempCorner);
        tempCorner.scale(1 / tempCorner.w);
        into.set(tempCorner);
        into.normalise();
    }

    private void setupMatrices(final int width, final int height) {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, width, height, 0, 1000, 3000);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0, 0, -2000);
        GlStateManager.viewport(0, 0, width, height);
    }

    private void restoreMatrices() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
    }

    private void getMatrix(final int matrix, final Matrix4f into) {
        float16Buffer.position(0);
        GlStateManager.getFloat(matrix, float16Buffer);
        float16Buffer.position(0);
        into.load(float16Buffer);
    }

    private void setUniform(final int uniform, final float value) {
        float1Buffer.clear();
        float1Buffer.put(value);
        float1Buffer.rewind();
        OpenGlHelper.glUniform1(uniform, float1Buffer);
    }

    private void setUniform(final int uniform, final Vec3d value) {
        float3Buffer.clear();
        float3Buffer.put((float) value.xCoord);
        float3Buffer.put((float) value.yCoord);
        float3Buffer.put((float) value.zCoord);
        float3Buffer.rewind();
        OpenGlHelper.glUniform3(uniform, float3Buffer);
    }
}
