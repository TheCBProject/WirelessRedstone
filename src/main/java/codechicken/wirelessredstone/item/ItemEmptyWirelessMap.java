package codechicken.wirelessredstone.item;

import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyWirelessMap extends ItemMapBase {

    public ItemEmptyWirelessMap() {
        setUnlocalizedName("wrcbe:empty_map");
        setCreativeTab(WirelessRedstone.creativeTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        ItemStack itemstack1 = new ItemStack(ModItems.itemWirelessMap, 1, worldIn.getUniqueDataId("map"));
        String s = "map_" + itemstack1.getMetadata();
        MapData mapdata = new MapData(s);
        worldIn.setData(s, mapdata);
        mapdata.scale = 0;
        mapdata.calculateMapCenter(playerIn.posX, playerIn.posZ, mapdata.scale);
        mapdata.dimension = worldIn.provider.getDimension();
        mapdata.trackingPosition = true;
        mapdata.unlimitedTracking = false;
        mapdata.markDirty();
        itemStackIn.shrink(1);

        if (itemStackIn.getCount() <= 0) {
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
        } else {
            if (!playerIn.inventory.addItemStackToInventory(itemstack1.copy())) {
                playerIn.dropItem(itemstack1, false);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
        }
    }
}
