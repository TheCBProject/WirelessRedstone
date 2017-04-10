package codechicken.wirelessredstone.item;

import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWirelessSniffer extends Item {
    public ItemWirelessSniffer() {
        setCreativeTab(WirelessRedstone.creativeTab);
        setUnlocalizedName("wrcbe:sniffer");
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            WirelessRedstone.proxy.openSnifferGui(player);
            RedstoneEtherAddons.client().addSniffer(player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
