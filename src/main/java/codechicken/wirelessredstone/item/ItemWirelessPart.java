package codechicken.wirelessredstone.item;

import java.util.List;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.TMultiPart;
import codechicken.wirelessredstone.part.JammerPart;
import codechicken.wirelessredstone.part.ReceiverPart;
import codechicken.wirelessredstone.part.TransmitterPart;
import codechicken.wirelessredstone.part.WirelessPart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWirelessPart extends JItemMultiPart
{
    public ItemWirelessPart() {
        setHasSubtypes(true);
        setUnlocalizedName("wrcbe:wireless_part");
    }

    @Override
    public TMultiPart newPart(ItemStack item, EntityPlayer player, World world, BlockPos pos, int side, Vector3 vhit) {
        BlockPos onPos = pos.offset(EnumFacing.VALUES[side ^ 1]);
        if (!world.isSideSolid(onPos, EnumFacing.VALUES[side]))
            return null;

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
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int d = 0; d < 3; d++)
            list.add(new ItemStack(item, 1, d));
    }

    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "|" + stack.getItemDamage();
    }
}
