package codechicken.wirelessredstone.item;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.TMultiPart;
import codechicken.wirelessredstone.part.JammerPart;
import codechicken.wirelessredstone.part.ReceiverPart;
import codechicken.wirelessredstone.part.TransmitterPart;
import codechicken.wirelessredstone.part.WirelessPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessPart extends JItemMultiPart {

    public ItemWirelessPart() {
        setHasSubtypes(true);
        setUnlocalizedName("wrcbe:wireless_part");
    }

    @Override
    public TMultiPart newPart(ItemStack item, EntityPlayer player, World world, BlockPos pos, int side, Vector3 vhit) {
        BlockPos onPos = pos.offset(EnumFacing.VALUES[side ^ 1]);
        if (!world.isSideSolid(onPos, EnumFacing.VALUES[side])) {
            return null;
        }

        WirelessPart part = getPart(item.getItemDamage());
        part.setupPlacement(player, side);
        return part;
    }

    public static WirelessPart getPart(int damage) {
        switch (damage) {
            case 0:
                return new TransmitterPart();
            case 1:
                return new ReceiverPart();
            case 2:
                return new JammerPart();
        }
        return null;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            for (int d = 0; d < 3; d++) {
                list.add(new ItemStack(this, 1, d));
            }
        }
    }

    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "|" + stack.getItemDamage();
    }
}
