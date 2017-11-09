package codechicken.wirelessredstone.client.bakery;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakery.generation.IItemBakery;
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

        return ItemQuadBakery.bakeItem(ImmutableList.of(TriangTexManager.getIconFromDamage(damage)));
    }
}
