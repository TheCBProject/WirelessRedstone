package codechicken.wirelessredstone.client.render.item;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.wirelessredstone.client.render.RenderTracker;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;

import static codechicken.lib.util.TransformUtils.*;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.*;

/**
 * Created by covers1624 on 15/02/2017.
 */
public class RenderItemTracker implements IItemRenderer {

    private static final CCModelState MODEL_STATE;

    static {
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();

        builder.put(GUI, compose(GUI, DEFAULT_BLOCK, create(16, 7, 16, 0, 0, 0, 2f)));
        builder.put(GROUND, compose(GROUND, DEFAULT_BLOCK, create(15.5f, 0, 15.5f, 0, 0, 0, 1.9f)));
        TRSRTransformation trans = create(15.5f, 8, 15.5f, 0, 0, 0, 1.9f);
        builder.put(FIRST_PERSON_RIGHT_HAND, compose(FIRST_PERSON_RIGHT_HAND, DEFAULT_BLOCK, trans));
        builder.put(THIRD_PERSON_RIGHT_HAND, compose(THIRD_PERSON_RIGHT_HAND, DEFAULT_BLOCK, trans));
        builder.put(FIRST_PERSON_LEFT_HAND, flipLeft(compose(FIRST_PERSON_RIGHT_HAND, DEFAULT_BLOCK, trans)));
        builder.put(THIRD_PERSON_LEFT_HAND, flipLeft(compose(THIRD_PERSON_RIGHT_HAND, DEFAULT_BLOCK, trans)));
        builder.put(FIXED, compose(FIXED, DEFAULT_BLOCK, trans));

        MODEL_STATE = new CCModelState(builder.build());
    }

    private static TRSRTransformation compose(TransformType type, IModelState parent, TRSRTransformation child) {
        Optional<TRSRTransformation> t = parent.apply(Optional.of(type));
        TRSRTransformation transform = t.orElseGet(TRSRTransformation::identity);
        return transform.compose(child);
    }

    @Override
    public IModelState getTransforms() {
        return MODEL_STATE;
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
    public void renderItem(ItemStack item, TransformType transformType) {
        RenderTracker.renderTracker(item.getItemDamage());
    }
}
