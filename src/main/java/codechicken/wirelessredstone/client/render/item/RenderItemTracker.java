package codechicken.wirelessredstone.client.render.item;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.wirelessredstone.client.render.RenderTracker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

import static codechicken.lib.util.TransformUtils.get;
import static codechicken.lib.util.TransformUtils.leftify;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.*;

/**
 * Created by covers1624 on 15/02/2017.
 */
public class RenderItemTracker implements IItemRenderer, IPerspectiveAwareModel {

    private static final CCModelState MODEL_STATE;

    static {
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        ImmutableMap<TransformType, TRSRTransformation> defaults = TransformUtils.DEFAULT_BLOCK.getTransforms();

        builder.put(GUI, defaults.get(GUI).compose(get(16, 7, 16, 0, 0, 0, 2f)));
        builder.put(GROUND, defaults.get(GROUND).compose(get(15.5f, 0, 15.5f, 0, 0, 0, 1.9f)));
        builder.put(FIRST_PERSON_RIGHT_HAND, defaults.get(FIRST_PERSON_RIGHT_HAND).compose(get(15.5f, 8, 15.5f, 0, 0, 0, 1.9f)));
        builder.put(THIRD_PERSON_RIGHT_HAND, defaults.get(THIRD_PERSON_RIGHT_HAND).compose(get(15.5f, 8, 15.5f, 0, 0, 0, 1.9f)));
        builder.put(FIRST_PERSON_LEFT_HAND, leftify(defaults.get(FIRST_PERSON_RIGHT_HAND).compose(get(15.5f, 8, 15.5f, 0, 0, 0, 1.9f))));
        builder.put(THIRD_PERSON_LEFT_HAND, leftify(defaults.get(THIRD_PERSON_RIGHT_HAND).compose(get(15.5f, 8, 15.5f, 0, 0, 0, 1.9f))));
        builder.put(FIXED, defaults.get(FIXED).compose(get(15.5f, 8, 15.5f, 0, 0, 0, 1.9f)));

        MODEL_STATE = new CCModelState(builder.build());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return ImmutableList.of();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, MODEL_STATE, cameraTransformType);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public void renderItem(ItemStack item) {
        RenderTracker.renderTracker(item.getItemDamage());
    }
}
