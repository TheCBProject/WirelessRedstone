package codechicken.wirelessredstone.client.bakery;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.ItemModelBakery;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.wirelessredstone.client.texture.RemoteTexManager;
import codechicken.wirelessredstone.item.ItemWirelessRemote;
import codechicken.wirelessredstone.manager.RedstoneEther;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Created by covers1624 on 13/02/2017.
 */
public class WirelessRemoteBakery implements IItemBakery {

    public static final WirelessRemoteBakery INSTANCE = new WirelessRemoteBakery();

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        TextureAtlasSprite sprite;
        int freq = stack.getItemDamage();
        if (freq <= 0 || freq > RedstoneEther.numfreqs) {
            sprite = RemoteTexManager.getIcon(-1, false);
        } else {
            sprite = RemoteTexManager.getIcon(RedstoneEther.get(true).getFreqColourId(freq), ItemWirelessRemote.getTransmitting(stack));
        }
        return ItemModelBakery.bakeItem(ImmutableList.of(sprite));
    }

    @Override
    public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {
        return PerspectiveAwareModelProperties.DEFAULT_ITEM;
    }
}
