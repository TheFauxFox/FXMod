package dev.paw.fxmod.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.paw.fxmod.FXMod;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Render3d {
    public static void enable(WorldRenderContext context) {
        Camera mainCamera = FXMod.MC.gameRenderer.getCamera();
        MatrixStack stack = context.matrixStack();
        Vec3d camera = mainCamera.getPos();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        GL11.glLineWidth(1);
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        stack.push();

        RenderSystem.applyModelViewMatrix();
        stack.translate(-camera.x, -camera.y, -camera.z);
    }

    public static void disable(WorldRenderContext context) {
        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack stack = context.matrixStack();

        tessellator.draw();
        stack.pop();
        RenderSystem.disableBlend();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.lineWidth(1.0F);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void drawBox(WorldRenderContext context, BlockPos pos, Color color) {
        drawBox(context, new Box(pos), color);
    }

    public static void drawTag(BlockPos pos, String text) {
        if (FXMod.MC.world == null) return;
        ArmorStandEntity tagStand = new ArmorStandEntity(FXMod.MC.world, pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5);
        tagStand.setInvisible(true);
        tagStand.setCustomNameVisible(true);
        tagStand.setCustomName(Text.of(text));
        FXMod.MC.world.addEntity(Block.getRawIdFromState(FXMod.MC.world.getBlockState(pos)), tagStand);
        FXMod.VARS.renderedFakeNametags.add(tagStand);
    }

    public static void drawBox(WorldRenderContext context, Box bb, Color color) {
        WorldRenderer.drawBox(context.matrixStack(), Tessellator.getInstance().getBuffer(), bb, color.getNormRed(), color.getNormGreen(), color.getNormBlue(), color.getNormAlpha());
    }
}