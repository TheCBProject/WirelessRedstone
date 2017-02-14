package codechicken.wirelessredstone.client.render;

import java.util.Iterator;

import codechicken.lib.texture.TextureUtils;
import codechicken.wirelessredstone.entity.WirelessBolt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.entity.WirelessBolt.Segment;

public class RenderWirelessBolt
{
    private static Vector3 getRelativeViewVector(Vector3 pos)
    {
        Entity renderentity = Minecraft.getMinecraft().getRenderViewEntity();
        return new Vector3((float)renderentity.posX - pos.x, (float)renderentity.posY + renderentity.getEyeHeight() - pos.y, (float)renderentity.posZ - pos.z);
    }
    
    public static void render(float frame, Entity entity)
    {
        CCRenderState ccrs = CCRenderState.instance();
        GlStateManager.pushMatrix();
        RenderUtils.translateToWorldCoords(entity, frame);

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        ccrs.reset();
        ccrs.brightness = 0xF000F0;
        TextureUtils.changeTexture("wrcbe:textures/lightning_glowstone.png");
        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        for(WirelessBolt bolt : WirelessBolt.clientboltlist)
            renderBolt(ccrs.getBuffer(), bolt, 0, 0xF000F0);
        ccrs.draw();

        TextureUtils.changeTexture("wrcbe:textures/lightning_redstone.png");
        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        for(WirelessBolt bolt : WirelessBolt.clientboltlist)
            renderBolt(ccrs.getBuffer(), bolt, 1, 0xF000F0);
        ccrs.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }
    
    private static void renderBolt(VertexBuffer buffer, WirelessBolt bolt, int pass, int brightness)
    {
        float boltage = bolt.particleAge < 0 ? 0 : (float)bolt.particleAge / (float)bolt.particleMaxAge;
        float mainalpha = 1;
        if(pass == 0)
            mainalpha = (1 - boltage) * 0.4F;
        else
            mainalpha = 1 - boltage * 0.5F;

        int l1 = brightness >> 16 & 65535;
        int l2 = brightness & 65535;
        
        int expandTime = (int)(bolt.length*WirelessBolt.speed);    
        int renderstart = (int) ((expandTime/2-bolt.particleMaxAge+bolt.particleAge) / (float)(expandTime/2) * bolt.numsegments0);
        int renderend = (int) ((bolt.particleAge+expandTime) / (float)expandTime * bolt.numsegments0);
        
        for(Iterator<Segment> iterator = bolt.segments.iterator(); iterator.hasNext();)
        {
            Segment renderSegment = iterator.next();
            
            if(renderSegment.segmentno < renderstart || renderSegment.segmentno > renderend)
                continue;
            
            Vector3 playervec = getRelativeViewVector(renderSegment.startpoint.point).negate();
            
            double width = 0.025F * (playervec.mag() / 5+1) * (1+renderSegment.light)*0.5F;
            
            Vector3 diff1 = playervec.copy().crossProduct(renderSegment.prevdiff).normalize().multiply(width / renderSegment.sinprev);
            Vector3 diff2 = playervec.copy().crossProduct(renderSegment.nextdiff).normalize().multiply(width / renderSegment.sinnext);
            
            Vector3 startvec = renderSegment.startpoint.point;
            Vector3 endvec = renderSegment.endpoint.point;
            
            //t.setColorRGBA_F(1, 1, 1, mainalpha * renderSegment.light);
            
            buffer.pos(endvec.x - diff2.x, endvec.y - diff2.y, endvec.z - diff2.z).tex(0.5, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            buffer.pos(startvec.x - diff1.x, startvec.y - diff1.y, startvec.z - diff1.z).tex(0.5, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            buffer.pos(startvec.x + diff1.x, startvec.y + diff1.y, startvec.z + diff1.z).tex(0.5, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            buffer.pos(endvec.x + diff2.x, endvec.y + diff2.y, endvec.z + diff2.z).tex(0.5, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            
            if(renderSegment.next == null)
            {
                Vector3 roundend = renderSegment.endpoint.point.copy().add(renderSegment.diff.copy().normalize().multiply(width));

                buffer.pos(roundend.x - diff2.x, roundend.y - diff2.y, roundend.z - diff2.z).tex(0, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(endvec.x - diff2.x, endvec.y - diff2.y, endvec.z - diff2.z).tex(0.5, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(endvec.x + diff2.x, endvec.y + diff2.y, endvec.z + diff2.z).tex(0.5, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(roundend.x + diff2.x, roundend.y + diff2.y, roundend.z + diff2.z).tex(0, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            }
            
            if(renderSegment.prev == null)
            {
                Vector3 roundend = renderSegment.startpoint.point.copy().subtract(renderSegment.diff.copy().normalize().multiply(width));

                buffer.pos(startvec.x - diff1.x, startvec.y - diff1.y, startvec.z - diff1.z).tex(0.5, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(roundend.x - diff1.x, roundend.y - diff1.y, roundend.z - diff1.z).tex(0, 0).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(roundend.x + diff1.x, roundend.y + diff1.y, roundend.z + diff1.z).tex(0, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
                buffer.pos(startvec.x + diff1.x, startvec.y + diff1.y, startvec.z + diff1.z).tex(0.5, 1).lightmap(l1, l2).color(1, 1, 1, mainalpha * renderSegment.light).endVertex();
            }
        }
    }
}
