package com.example.testmod.block.scroll_forge;

import com.example.testmod.TestMod;
import com.example.testmod.item.InkItem;
import com.example.testmod.util.ModTags;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static net.minecraft.core.Direction.SOUTH;


public class ScrollForgeRenderer implements BlockEntityRenderer<ScrollForgeTile> {
    private static final ResourceLocation PAPER_TEXTURE = new ResourceLocation(TestMod.MODID , "textures/block/scroll_forge_paper.png");
    ItemRenderer itemRenderer;

    public ScrollForgeRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    private static final Vec3 INK_POS = new Vec3(.175, .876, .25);
    private static final Vec3 FOCUS_POS = new Vec3(.75, .876, .4);
    private static final Vec3 PAPER_POS = new Vec3(.5, .876, .7);

    @Override
    public void render(ScrollForgeTile scrollForgeTile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack inkStack = scrollForgeTile.getStackInSlot(0);
        ItemStack paperStack = scrollForgeTile.getStackInSlot(1);
        ItemStack focusStack = scrollForgeTile.getItemHandler().getStackInSlot(2);

        if (!inkStack.isEmpty() && inkStack.getItem() instanceof InkItem) {
            renderItem(inkStack, INK_POS, 15, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
        if (!focusStack.isEmpty() && focusStack.is(ModTags.SCHOOL_FOCUS)) {
            renderItem(focusStack, FOCUS_POS, 5, scrollForgeTile, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }

        if (!paperStack.isEmpty() && paperStack.is(Items.PAPER)) {
            poseStack.pushPose();
            RenderSystem.disableCull();
            rotatePoseWithBlock(poseStack,scrollForgeTile);
            poseStack.translate(PAPER_POS.x, PAPER_POS.y, PAPER_POS.z);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(85));
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(PAPER_TEXTURE));

            drawQuad(.45f, poseStack.last(), consumer);
            poseStack.popPose();
        }


    }

    private void renderItem(ItemStack itemStack, Vec3 offset, float yRot, ScrollForgeTile scrollForgeTile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        //renderId seems to be some kind of uuid/salt
        int renderId = (int) scrollForgeTile.getBlockPos().asLong();
        //BakedModel model = itemRenderer.getModel(itemStack, null, null, renderId);

        rotatePoseWithBlock(poseStack,scrollForgeTile);

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(yRot));

        //poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.scale(0.45f, 0.45f, 0.45f);

        itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, LevelRenderer.getLightColor(scrollForgeTile.getLevel(), scrollForgeTile.getBlockPos().above()), packedOverlay, poseStack, bufferSource, renderId);
        poseStack.popPose();
    }

    private void drawQuad(float width, PoseStack.Pose pose, VertexConsumer consumer) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        float halfWidth = width * .5f;
        consumer.vertex(poseMatrix, -halfWidth, 0, -halfWidth).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, -halfWidth).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, halfWidth).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 0, halfWidth).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normalMatrix, 0f, 1f, 0f).endVertex();

    }

    private void rotatePoseWithBlock(PoseStack poseStack, ScrollForgeTile scrollForgeTile){
        Vec3 center = new Vec3(0.5, 0.5, 0.5);
        poseStack.translate(center.x, center.y, center.z);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(getBlockFacingDegrees(scrollForgeTile)));
        poseStack.translate(-center.x, -center.y, -center.z);
    }
    private int getBlockFacingDegrees(ScrollForgeTile tileEntity) {
        var block = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
        if (block.getBlock() instanceof ScrollForgeBlock) {
            var facing = block.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return switch (facing) {
                case NORTH -> 180;
                case EAST -> 90;
                case WEST -> -90;
                default -> 0;
            };
        } else
            return 0;

    }
}
