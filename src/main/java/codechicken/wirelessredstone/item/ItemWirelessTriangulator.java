package codechicken.wirelessredstone.item;

import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.client.bakery.WirelessTriangulatorBakery;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessTriangulator extends ItemWirelessFreq implements IBakeryProvider {

    public ItemWirelessTriangulator() {
        setCreativeTab(WirelessRedstone.creativeTab);
        setUnlocalizedName("wrcbe:triangulator");
        setMaxStackSize(1);
    }

    public int getItemFreq(ItemStack itemstack) {
        return itemstack.getItemDamage();
    }

    @SideOnly (Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        return RedstoneEtherAddons.localizeWirelessItem(I18n.translateToLocal("item.wrcbe.triangulator.short"), itemstack.getItemDamage());
    }

    public String getGuiName() {
        return I18n.translateToLocal("item.wrcbe:triangulator.name");
    }

    @Override
    public IBakery getBakery() {
        return WirelessTriangulatorBakery.INSTANCE;
    }
}
