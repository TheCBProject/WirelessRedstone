package codechicken.wirelessredstone.addons;

import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPrivateSniffer extends Item
{
    public ItemPrivateSniffer() {
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            WirelessRedstoneAddons.proxy.openPSnifferGui(player);
            RedstoneEtherAddons.client().addSniffer(player);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }
}
