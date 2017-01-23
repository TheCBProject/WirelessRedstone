package codechicken.wirelessredstone.addons;

import codechicken.wirelessredstone.core.ItemWirelessFreq;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWirelessTracker extends ItemWirelessFreq
{
    public ItemWirelessTracker() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        if (getItemFreq(itemStack) == 0)
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);

        if (!player.capabilities.isCreativeMode) {
            itemStack.stackSize--;
        }
        if (!world.isRemote) {
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            EntityWirelessTracker tracker = new EntityWirelessTracker(world, getItemFreq(itemStack), player);
            world.spawnEntityInWorld(tracker);
            WRAddonSPH.sendThrowTracker(tracker, player);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }


    @Override
    public int getItemFreq(ItemStack itemstack) {
        return itemstack.getItemDamage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        return RedstoneEtherAddons.localizeWirelessItem(
                I18n.translateToLocal("wrcbe_addons.tracker.short"),
                itemstack.getItemDamage());
    }

    @Override
    public String getGuiName() {
        return I18n.translateToLocal("item.wrcbe_addons:tracker.name");
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerIcons(IIconRegister par1IconRegister) {
    //}
}
