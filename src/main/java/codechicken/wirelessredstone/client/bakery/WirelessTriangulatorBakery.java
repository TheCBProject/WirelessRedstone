package codechicken.wirelessredstone.client.bakery;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.ItemModelBakery;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.wirelessredstone.client.texture.TriangTexManager;
import codechicken.wirelessredstone.manager.RedstoneEther;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Created by covers1624 on 15/02/2017.
 */
public class WirelessTriangulatorBakery implements IItemBakery {

    public static final WirelessTriangulatorBakery INSTANCE = new WirelessTriangulatorBakery();

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

        int damage = stack.getItemDamage();
        if (damage < 0 || damage > RedstoneEther.numfreqs) {
            damage = 0;
        }

        return ItemModelBakery.bakeItem(ImmutableList.of(TriangTexManager.getIconFromDamage(damage)));
    }

    @Override
    public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {
        return PerspectiveAwareModelProperties.DEFAULT_ITEM;
    }
}
