package codechicken.wirelessredstone.part;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.client.render.RenderWireless;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TransceiverPart extends WirelessPart implements ITileWireless//, IPeripheral
{

    public byte deadmap;
    public int currentfreq;

    @Override
    public int getFreq() {
        return currentfreq;
    }

    @Override
    public void setFreq(int newfreq) {
        removeFromEther();
        currentfreq = newfreq;
        addToEther();
        if (disabled()) {
            RedstoneEther.server().jamNode(world(), pos(), newfreq);
        }
        updateChange();
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        currentfreq = tag.getInteger("freq");
        deadmap = tag.getByte("deadmap");
    }

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        tag.setInteger("freq", currentfreq);
        tag.setByte("deadmap", deadmap);
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        super.writeDesc(packet);
        packet.writeShort(currentfreq);
    }

    @Override
    public void readDesc(MCDataInput packet) {
        super.readDesc(packet);
        currentfreq = packet.readUShort();
    }

    @Override
    public void jamTile() {
        setDisabled(true);
        deadmap = (byte) world().rand.nextInt(256);
        scheduleTick(3);
    }

    @Override
    public void unjamTile() {
        if (disabled()) {
            deadmap = (byte) world().rand.nextInt(256);
            scheduleTick(3);
        }
        setDisabled(false);
    }

    @Override
    public void scheduledTick() {
        if (deadmap != 0) {
            deadmap = (byte) ((deadmap & 0xFF) >> 1);
            if (deadmap != 0) {
                scheduleTick(3);
            }

            updateChange();
        }
    }

    @Override
    public boolean activate(EntityPlayer player, CuboidRayTraceResult hit, ItemStack held, EnumHand hand) {
        if (super.activate(player, hit, held, hand)) {
            return true;
        }

        if (hit.sideHit.ordinal() == (side() ^ 1) && !player.isSneaking()) {
            if (world().isRemote) {
                WirelessRedstone.proxy.openTileWirelessGui(player, (ITileWireless) tile());
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void renderDynamic(Vector3 pos, int pass, float frame) {
        super.renderDynamic(pos, pass, frame);
        if (pass == 0) {
            RenderWireless.renderFreq(pos, this);
        }
    }
}
