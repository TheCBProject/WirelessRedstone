package codechicken.wirelessredstone.client.render;

import codechicken.lib.render.*;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.uv.IconTransformation;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.SwapYZ;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

import static codechicken.lib.math.MathHelper.*;

public class RenderTracker extends RenderEntity
{
    private static CCModel model;
    
    static
    {
        model = OBJParser.parseModels(new ResourceLocation("wrcbe", "models/tracker.obj"), 7, new SwapYZ()).get("Tracker");
        model.apply(new Translation(0, 0.1875, 0));
    }

    public RenderTracker(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }
    
    public static void renderTracker(int freq)
    {
        GlStateManager.disableLighting();

        TextureUtils.bindBlockTexture();
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        ccrs.colour = 0xFFFFFFFF;
        model.render(ccrs, new IconTransformation(TextureUtils.getBlockTexture("obsidian")));
        ccrs.draw();
        
        Matrix4 pearlMat = RenderUtils.getMatrix(
            new Vector3(0, 0.44+RedstoneEther.getSineWave(ClientUtils.getRenderTime(), 7)*0.02, 0),
            new Rotation(RedstoneEther.getRotation(ClientUtils.getRenderTime(), freq), new Vector3(0, 1, 0)),
            0.04);

        TextureUtils.changeTexture("wrcbe:textures/hedronmap.png");
        ccrs.startDrawing(4, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        ccrs.colour = freq == 0 ? 0xC0C0C0FF : 0xFFFFFFFF;
        CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
        ccrs.draw();

        GlStateManager.enableLighting();
    }
    
    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float f1)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y+0.2, z);
        
        EntityWirelessTracker tracker = (EntityWirelessTracker) entity;
        if(tracker.isAttachedToEntity())
        {
            Vector3 relVec = tracker.getRotatedAttachment();
                        
            Vector3 yAxis = new Vector3(0, 1, 0);
            Vector3 axis = relVec.copy().crossProduct(yAxis);
            double angle = -(relVec.angle(yAxis)*todeg);

            GlStateManager.translate(-x, -y-0.2, -z);//undo translation
            
            Vector3 pos = new Vector3(tracker.attachedEntity.lastTickPosX + (tracker.attachedEntity.posX - tracker.attachedEntity.lastTickPosX)*f1, 
                    tracker.attachedEntity.lastTickPosY + (tracker.attachedEntity.posY - tracker.attachedEntity.lastTickPosY)*f1 + tracker.attachedEntity.height/2 - tracker.attachedEntity.getEyeHeight() - tracker.height,
                    tracker.attachedEntity.lastTickPosZ + (tracker.attachedEntity.posZ - tracker.attachedEntity.lastTickPosZ)*f1);
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            pos.add(relVec).add(-manager.renderPosX, -manager.renderPosY, -manager.renderPosZ);//TODO these are just interpolated player positions, Create helper, rule the world.

            GlStateManager.translate(pos.x, pos.y, pos.z);

            GlStateManager.rotate((float)angle, (float)axis.x, (float)axis.y, (float)axis.z);
        }
        else if(tracker.item)
        {
            double bob = sin(ClientUtils.getRenderTime() / 10) * 0.1;
            double rotate = ClientUtils.getRenderTime() / 20 * todeg;

            GlStateManager.rotate((float) rotate, 0, 1, 0);
            GlStateManager.translate(0, bob + 0.2, 0);
        }
        GlStateManager.translate(0, -0.2, 0);
        renderTracker(tracker.freq);
        GlStateManager.popMatrix();
    }
}
