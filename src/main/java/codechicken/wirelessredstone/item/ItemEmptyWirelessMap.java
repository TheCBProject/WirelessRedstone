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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemstack1 = new ItemStack(ModItems.itemWirelessMap, 1, worldIn.getUniqueDataId("map"));
        String s = "map_" + itemstack1.getItemDamage();
        MapData mapdata = new MapData(s);
        worldIn.setItemData(s, mapdata);
        mapdata.scale = 0;
        int i = 128 * (1 << mapdata.scale);
        mapdata.xCenter = (int)(Math.round(playerIn.posX / (double)i) * (long)i);
        mapdata.zCenter = (int)(Math.round(playerIn.posZ / (double)i) * (long)i);
        mapdata.dimension = worldIn.provider.getDimension();
        mapdata.markDirty();
        --itemStackIn.stackSize;

        if (itemStackIn.stackSize <= 0)
        {
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack1);
        }
        else
        {
            if (!playerIn.inventory.addItemStackToInventory(itemstack1.copy()))
            {
                playerIn.dropItem(itemstack1, false);
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
    }
}
