package codechicken.wirelessredstone.item;

import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.network.WRClientPH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemWirelessFreq extends Item
{

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking())
            return EnumActionResult.PASS;

        WirelessRedstone.proxy.openItemWirelessGui(player);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            WirelessRedstone.proxy.openItemWirelessGui(player);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        }

        return super.onItemRightClick(itemStack, world, player, hand);
    }

    public final void setFreq(EntityPlayer player, int slot, ItemStack stack, int freq) {
        if (player.worldObj.isRemote)
            WRClientPH.sendSetItemFreq(slot, freq);
        else
            stack.setItemDamage(freq);
    }

    public abstract int getItemFreq(ItemStack itemstack);

    public abstract String getGuiName();
}
