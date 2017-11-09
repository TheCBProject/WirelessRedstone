package codechicken.wirelessredstone.item;

import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import codechicken.wirelessredstone.network.WRServerPH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessTracker extends ItemWirelessFreq {

    public ItemWirelessTracker() {
        setMaxStackSize(1);
        setUnlocalizedName("wrcbe:tracker");
        setCreativeTab(WirelessRedstone.creativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            return super.onItemRightClick(world, player, hand);
        }

        if (getItemFreq(itemStack) == 0) {
            return new ActionResult<>(EnumActionResult.PASS, itemStack);
        }

        if (!player.capabilities.isCreativeMode) {
            itemStack.shrink(1);
        }
        if (!world.isRemote) {
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            EntityWirelessTracker tracker = new EntityWirelessTracker(world, getItemFreq(itemStack), player);
            world.spawnEntity(tracker);
            WRServerPH.sendThrowTracker(tracker, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public int getItemFreq(ItemStack itemstack) {
        return itemstack.getItemDamage();
    }

    @SideOnly (Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        return RedstoneEtherAddons.localizeWirelessItem(I18n.translateToLocal("item.wrcbe.tracker.short"), itemstack.getItemDamage());
    }

    @Override
    public String getGuiName() {
        return I18n.translateToLocal("item.wrcbe:tracker.name");
    }
}
