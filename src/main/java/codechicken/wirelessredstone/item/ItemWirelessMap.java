package codechicken.wirelessredstone.item;

import codechicken.lib.util.ClientUtils;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessMap extends ItemMap {

    public ItemWirelessMap() {
        setUnlocalizedName("wrcbe:map");
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int slotno, boolean held) {
        super.onUpdate(itemstack, world, entity, slotno, held);
        EntityPlayer player = (EntityPlayer) entity;
        if (held) {
            if (slotno != lastheldmap) {
                if (ClientUtils.inWorld()) {
                    RedstoneEtherAddons.client().clearMapNodes(player);
                    lastheldmap = slotno;
                }
            }
        } else {
            ItemStack helditem = player.inventory.getCurrentItem();
            if ((helditem == null || helditem.getItem() != this) && lastheldmap >= 0) {
                if (ClientUtils.inWorld()) {
                    RedstoneEtherAddons.client().clearMapNodes(player);
                    lastheldmap = -1;
                }
            }
        }
    }

    @Override
    public Packet createMapDataPacket(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        RedstoneEtherAddons.server().updateSMPMapInfo(world, entityplayer, getMapData(itemstack, world), itemstack.getItemDamage());
        return super.createMapDataPacket(itemstack, world, entityplayer);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return super.getItemStackDisplayName(stack) + " #" + stack.getItemDamage();
    }

    public int lastheldmap = -1;
}
