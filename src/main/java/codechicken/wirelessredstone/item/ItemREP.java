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

public class ItemREP extends Item
{
    public ItemREP() {
        setMaxStackSize(16);
        setUnlocalizedName("wrcbe:rep");
        setCreativeTab(WirelessRedstone.creativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);

        if (RedstoneEtherAddons.server().detonateREP(player))
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);

        RedstoneEtherAddons.server().throwREP(itemStack, world, player);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }
}
