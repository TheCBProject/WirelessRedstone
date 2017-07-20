package codechicken.wirelessredstone.client.render.item;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.wirelessredstone.client.render.RenderWireless;
import codechicken.wirelessredstone.item.ItemWirelessPart;
import codechicken.wirelessredstone.part.WirelessPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class RenderItemWireless implements IItemRenderer {

    private WirelessPart[] renderParts = new WirelessPart[3];

    public RenderItemWireless() {
        for (int i = 0; i < renderParts.length; i++) {
            renderParts[i] = ItemWirelessPart.getPart(i).setRotation(2);
        }
    }

    @Override
    public void renderItem(ItemStack item, TransformType transformType) {
        RenderWireless.renderInv(CCRenderState.instance(), renderParts[item.getItemDamage()]);
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }
}
