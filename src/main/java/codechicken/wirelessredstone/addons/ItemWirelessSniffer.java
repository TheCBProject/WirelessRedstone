package codechicken.wirelessredstone.addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWirelessSniffer extends Item
{
    public ItemWirelessSniffer() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            WirelessRedstoneAddons.proxy.openSnifferGui(player);
            RedstoneEtherAddons.client().addSniffer(player);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }
}
