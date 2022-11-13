function initializeCoreMod() {
    var Opcodes = Java.type("org.objectweb.asm.Opcodes");
    var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var renderLevel = ASMAPI.mapMethod("m_109599_"); // renderLevel
    var endOutlineBatch = ASMAPI.mapMethod("m_109928_"); // endOutlineBatch

    return {
        "Render Scan Effect": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.LevelRenderer",
                "methodName": renderLevel,
                "methodDesc": "(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V"
            },
            "transformer": function(methodNode) {
                ASMAPI.insertInsnList(methodNode,
                    // The following is conveniently unique and comes after all solid rendering and before
                    // Fabulous mode shaders have a chance to screw up the depth buffer.
                    // INVOKEVIRTUAL net/minecraft/client/renderer/OutlineLayerBuffer.endOutlineBatch ()V
                    ASMAPI.MethodType.VIRTUAL, "net/minecraft/client/renderer/OutlineBufferSource", endOutlineBatch, "()V",
                    // We inject the call to ScanManager.render, which takes the matrix stack and the projection matrix.
                    // The method we inject into gets these as parameters, too, so we luckily load them by their indices.
                    ASMAPI.listOf(
                        new VarInsnNode(Opcodes.ALOAD, 1), // Load MatrixStack (first argument)
                        ASMAPI.buildMethodCall("li/cil/scannable/client/renderer/ScannerRenderer", "render", "(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ASMAPI.MethodType.STATIC)
                    ),
                    ASMAPI.InsertMode.INSERT_BEFORE);

                return methodNode;
            }
        }
    };
}