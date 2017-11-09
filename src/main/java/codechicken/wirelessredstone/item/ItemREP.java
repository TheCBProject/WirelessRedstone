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

public class ItemREP extends Item {

    public ItemREP() {
        setMaxStackSize(16);
        setUnlocalizedName("wrcbe:rep");
        setCreativeTab(WirelessRedstone.creativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if (RedstoneEtherAddons.server().detonateREP(player)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        RedstoneEtherAddons.server().throwREP(stack, world, player);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
