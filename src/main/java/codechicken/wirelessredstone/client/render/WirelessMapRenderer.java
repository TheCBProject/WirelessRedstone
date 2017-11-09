package codechicken.wirelessredstone.client.render;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.map.IMapRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.wirelessredstone.api.ClientMapInfo;
import codechicken.wirelessredstone.api.FreqCoord;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.item.ItemWirelessMap;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import codechicken.wirelessredstone.util.WirelessMapNodeStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

import javax.annotation.Nullable;

public class WirelessMapRenderer implements IMapRenderer {

    private void renderPass(CCRenderState ccrs, int xCenter, int zCenter, int scale, WirelessMapNodeStorage mapstorage, float size, float alpha, float light) {
        BufferBuilder buffer = ccrs.getBuffer();
        float blockscale = 1 << scale;

        for (FreqCoord node : mapstorage.nodes) {
            float relx = node.x / blockscale + 64;
            float relz = node.z / blockscale + 64;

            int colour = RedstoneEther.client().getFreqColour(node.freq);
            if (colour == 0xFFFFFFFF) {
                colour = 0xFFFF0000;
            }
            float r = ((colour >> 16) & 0xFF) / 255F * light;
            float g = ((colour >> 8) & 0xFF) / 255F * light;
            float b = (colour & 0xFF) / 255F * light;

            float rot = RedstoneEther.getRotation(ClientUtils.getRenderTime(), node.freq);
            float xrot = (float) (Math.sin(rot) * size);
            float zrot = (float) (Math.cos(rot) * size);

            buffer.pos(relx - zrot, relz + xrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx + xrot, relz + zrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx + zrot, relz - xrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx - xrot, relz - zrot, -0.01).color(r, g, b, alpha).endVertex();
        }

        for (FreqCoord node : mapstorage.devices) {
            float relx = (node.x - xCenter) / blockscale + 64;
            float relz = (node.z - zCenter) / blockscale + 64;

            int colour = RedstoneEther.client().getFreqColour(node.freq);
            if (colour == 0xFFFFFFFF) {
                colour = 0xFFFF0000;
            }
            float r = ((colour >> 16) & 0xFF) / 255F * light;
            float g = ((colour >> 8) & 0xFF) / 255F * light;
            float b = (colour & 0xFF) / 255F * light;

            float rot = RedstoneEther.getRotation(ClientUtils.getRenderTime(), node.freq);
            float xrot = (float) (Math.sin(rot) * size);
            float zrot = (float) (Math.cos(rot) * size);

            buffer.pos(relx - zrot, relz + xrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx + xrot, relz + zrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx + zrot, relz - xrot, -0.01).color(r, g, b, alpha).endVertex();
            buffer.pos(relx - xrot, relz - zrot, -0.01).color(r, g, b, alpha).endVertex();
        }
    }

    public void render(ItemStack stack, MapData data, boolean inFrame) {
        WirelessMapNodeStorage mapstorage = RedstoneEtherAddons.client().getMapNodes();

        if (stack == null || stack.getItem() != ModItems.itemWirelessMap) {
            return;
        }

        ClientMapInfo mapinfo = RedstoneEtherAddons.client().getMPMapInfo((short) stack.getItemDamage());
        if (mapinfo == null) {
            return;
        }

        CCRenderState ccrs = CCRenderState.instance();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
        float light = 1;

        int xCenter = mapinfo.xCenter;
        int zCenter = mapinfo.zCenter;
        int scale = mapinfo.scale;

        ccrs.startDrawing(0x07, DefaultVertexFormats.POSITION_COLOR);
        renderPass(ccrs, xCenter, zCenter, scale, mapstorage, 0.75F, 1F, light * 0.5F);
        renderPass(ccrs, xCenter, zCenter, scale, mapstorage, 0.6F, 1F, light);
        ccrs.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldHandle(ItemStack stack, @Nullable MapData data, boolean inFrame) {
        return stack.getItem() instanceof ItemWirelessMap;
    }

    @Override
    public void renderMap(ItemStack stack, @Nullable MapData data, boolean inFrame) {
        if (inFrame) {
            TextureUtils.changeTexture("textures/map/map_background.png");
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
            GlStateManager.translate(-64.0F, -64.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, -1.0F);
            if (data != null) {
                Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(data, inFrame);
            }
        } else {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.38F, 0.38F, 0.38F);
            GlStateManager.disableLighting();
            TextureUtils.changeTexture("textures/map/map_background.png");
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
            vertexbuffer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
            vertexbuffer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
            vertexbuffer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            if (data != null) {
                Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(data, inFrame);
            }
        }
        render(stack, data, inFrame);
    }
}
