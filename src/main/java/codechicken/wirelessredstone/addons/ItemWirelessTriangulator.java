package codechicken.wirelessredstone.addons;

import net.minecraft.item.ItemStack;
import codechicken.wirelessredstone.core.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessTriangulator extends ItemWirelessFreq
{
    public ItemWirelessTriangulator() {
        setMaxStackSize(1);
    }

    //public IIcon getIconFromDamage(int damage) {
    //    if (damage < 0 || damage > RedstoneEther.numfreqs)
    //        damage = 0;
    //    return TriangTexManager.getIconFromDamage(damage);
    //}

    public int getItemFreq(ItemStack itemstack) {
        return itemstack.getItemDamage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        return RedstoneEtherAddons.localizeWirelessItem(
                I18n.translateToLocal("wrcbe_addons.triangulator.short"),
                itemstack.getItemDamage());
    }

    public String getGuiName() {
        return I18n.translateToLocal("item.wrcbe_addons:triangulator.name");
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerIcons(IIconRegister par1IconRegister) {
    //}
}
