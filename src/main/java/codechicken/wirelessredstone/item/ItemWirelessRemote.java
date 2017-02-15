package codechicken.wirelessredstone.item;

import codechicken.lib.model.blockbakery.IBakeryItem;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.client.bakery.WirelessRemoteBakery;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessRemote extends ItemWirelessFreq implements IBakeryItem {
    public ItemWirelessRemote() {
        setCreativeTab(WirelessRedstone.creativeTab);
        setUnlocalizedName("wrcbe:remote");
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking() && stack.getItemDamage() <= 5000 && stack.getItemDamage() > 0)//not sneaking, off and valid freq
        {
            TileEntity tile = world.getTileEntity(pos);
            int freq = stack.getItemDamage();
            if (tile != null && tile instanceof ITileWireless && RedstoneEther.get(world.isRemote).canBroadcastOnFrequency(player, freq)) {
                RedstoneEther.get(world.isRemote).setFreq((ITileWireless) tile, freq);
                return EnumActionResult.SUCCESS;
            }
        }
        onItemRightClick(stack, world, player, hand);
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        if (!getTransmitting(itemStack) && itemStack.getItemDamage() != 0) {
            RedstoneEtherAddons.get(world.isRemote).activateRemote(world, player);
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        int freq = getItemFreq(stack);
        EntityPlayer player = (EntityPlayer) entity;

        if (getTransmitting(stack) && (!held || !RedstoneEtherAddons.get(world.isRemote).isRemoteOn(player, freq)) && !RedstoneEtherAddons.get(world.isRemote).deactivateRemote(world, player)) {
            stack.setItemDamage(freq);
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        return !getTransmitting(stack);
    }

    @Override
    public int getItemFreq(ItemStack itemstack) {
        return itemstack.getItemDamage() & 0x1FFF;
    }

    public static boolean getTransmitting(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("on", 1) && tag.getBoolean("on");
    }

    public static void setOn(ItemStack stack, boolean on) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("on", on);
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public IIcon getIcon(ItemStack stack, int pass) {
    //    return getIconIndex(stack);
    //}

    //@Override
    //@SideOnly(Side.CLIENT)
    //public IIcon getIconIndex(ItemStack stack) {
    //    int freq = stack.getItemDamage();
    //    if (freq <= 0 || freq > RedstoneEther.numfreqs)
    //        return RemoteTexManager.getIcon(-1, false);
    //    return RemoteTexManager.getIcon(RedstoneEther.get(true).getFreqColourId(freq), getTransmitting(stack));
    //}

    @Override
    @SideOnly (Side.CLIENT)
    public String getItemStackDisplayName(ItemStack itemstack) {
        return RedstoneEtherAddons.localizeWirelessItem(I18n.translateToLocal("item.wrcbe.remote.short"), itemstack.getItemDamage());
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerIcons(IIconRegister par1IconRegister) {
    //}

    @Override
    public String getGuiName() {
        return I18n.translateToLocal("item.wrcbe:remote.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemBakery getBakery() {
        return WirelessRemoteBakery.INSTANCE;
    }
}
