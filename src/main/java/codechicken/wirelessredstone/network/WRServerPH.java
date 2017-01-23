package codechicken.wirelessredstone.network;

import java.util.ArrayList;
import java.util.TreeSet;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IServerPacketHandler;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.entity.EntityREP;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import codechicken.wirelessredstone.api.FreqCoord;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.entity.WirelessBolt;
import codechicken.wirelessredstone.item.ItemWirelessFreq;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapData;

public class WRServerPH implements IServerPacketHandler
{

    @Override
    public void handlePacket(PacketCustom packet, EntityPlayerMP sender, INetHandlerPlayServer handler) {
        handlePacket((WorldServer) sender.worldObj, sender, packet);
    }

    private void handlePacket(WorldServer world, EntityPlayerMP player, PacketCustom packet) {
        switch (packet.getType()) {
            case 1:
                setTileFreq(player, world, packet.readPos(), packet.readShort());
                break;
            case 2:
                setItemFreq(player, packet.readShort(), packet.readShort());
                break;
            case 4:
                handleFreqInfo(packet);
                break;
            case 5:
                decrementSlot(player, packet.readShort());
                break;
            case 9:
                RedstoneEther.get(false).setFreqOwner(packet.readShort(), packet.readString());
                break;
            case 50:
                if (packet.readBoolean())
                    RedstoneEtherAddons.server().addSniffer(player);
                else
                    RedstoneEtherAddons.server().remSniffer(player);
                break;
            case 51:
                if (packet.readBoolean())
                    RedstoneEtherAddons.server().activateRemote(world, player);
                else
                    RedstoneEtherAddons.server().deactivateRemote(world, player);
                break;
            case 52:
                RedstoneEtherAddons.server().setTriangRequired(player, packet.readUShort(), packet.readBoolean());
                break;
            case 58:
                RedstoneEtherAddons.server().clearMapNodes(player);
                break;
        }
    }

    private void decrementSlot(EntityPlayerMP player, int slot) {
        try {
            ItemStack item = player.inventory.mainInventory[slot];
            item.stackSize--;

            if (item.stackSize == 0) {
                player.inventory.mainInventory[slot] = null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {}
    }

    private void setItemFreq(EntityPlayerMP sender, int slot, int freq) {
        if (RedstoneEther.get(false).canBroadcastOnFrequency(sender, freq)) {
            ItemStack stack = sender.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() instanceof ItemWirelessFreq) {
                ((ItemWirelessFreq) stack.getItem()).setFreq(sender, slot, stack, freq);
            }
        }
    }

    private void setTileFreq(EntityPlayer sender, World world, BlockPos pos, int freq) {
        if (RedstoneEther.get(false).canBroadcastOnFrequency(sender, freq)) {
            TileEntity tile = RedstoneEther.getTile(world, pos);
            if (tile instanceof ITileWireless)
                RedstoneEther.get(false).setFreq((ITileWireless) tile, freq);
        }
    }

    private void handleFreqInfo(PacketCustom packet) {
        int freq = packet.readUShort();
        String name = packet.readString();
        int colourid = packet.readUByte();

        RedstoneEther.get(false).setFreqName(freq, name);
        RedstoneEther.get(false).setFreqColour(freq, colourid);

        sendSetFreqInfoTo(null, freq, name, colourid);
    }

    public static void sendSetFrequencyRangeTo(EntityPlayer player, int startfreq, int endfreq, boolean jam) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 3);
        packet.writeShort((short) startfreq);
        packet.writeShort((short) endfreq);
        packet.writeBoolean(jam);

        packet.sendToPlayer(player);
    }

    public static void sendPublicFrequencyTo(EntityPlayer player, int freq) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 2);
        packet.writeShort(freq);
        packet.writeByte(1);

        packet.sendToPlayer(player);
    }

    public static void sendSharedFrequencyTo(EntityPlayer player, int freq) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 2);
        packet.writeShort(freq);
        packet.writeByte(2);

        packet.sendToPlayer(player);
    }

    public static void sendSetFreqInfoTo(EntityPlayer player, int freq, String name, int colourid) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 4);
        packet.writeShort(freq);
        packet.writeByte(colourid);
        packet.writeString(name);

        packet.sendToPlayer(player);
    }

    public static void sendJamPlayerPacketTo(EntityPlayer player, boolean jam) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 7);
        packet.writeBoolean(jam);

        packet.sendToPlayer(player);
    }

    public static void sendWirelessBolt(WirelessBolt bolt) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 8);
        packet.writeFloat((float) bolt.start.x);
        packet.writeFloat((float) bolt.start.y);
        packet.writeFloat((float) bolt.start.z);
        packet.writeFloat((float) bolt.end.x);
        packet.writeFloat((float) bolt.end.y);
        packet.writeFloat((float) bolt.end.z);
        packet.writeLong(bolt.seed);

        packet.sendToChunk(bolt.world, (int) bolt.start.x >> 4, (int) bolt.start.z >> 4);
    }

    public static void sendSetSlot(int slot, ItemStack stack) {
    }

    public static void sendFreqInfoTo(EntityPlayer player, ArrayList<Integer> freqsWithInfo) {
        if (freqsWithInfo.size() == 0)
            return;

        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 1);
        packet.writeShort(freqsWithInfo.size());
        for (int freq : freqsWithInfo) {
            packet.writeShort(freq);
            packet.writeByte(RedstoneEther.get(false).getFreqColourId(freq));
            packet.writeString(RedstoneEther.get(false).getFreqName(freq));
        }
        packet.sendToPlayer(player);
    }

    public static void sendFreqOwnerTo(EntityPlayer player, ArrayList<Integer> freqsWithOwners) {
        if (freqsWithOwners.size() == 0)
            return;

        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 10);
        packet.writeShort(freqsWithOwners.size());
        for (int freq : freqsWithOwners) {
            packet.writeShort(freq);
            packet.writeString(RedstoneEther.get(false).getFreqOwner(freq));
        }
        packet.sendToPlayer(player);
    }

    public static void sendSetFreqOwner(int freq, String username) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 9);
        packet.writeShort(freq);
        packet.writeString(username);

        packet.sendToClients();
    }
    public static void sendUpdateSnifferTo(EntityPlayer player, int freq, boolean on) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 53);
        packet.writeShort((short) freq);
        packet.writeBoolean(on);

        packet.sendToPlayer(player);
    }

    public static void sendEtherCopyTo(EntityPlayer player, byte[] ethercopy) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 54);
        packet.writeShort(ethercopy.length);
        packet.writeByteArray(ethercopy);

        packet.sendToPlayer(player);
    }

    public static void sendTriangAngleTo(EntityPlayer player, int freq, float angle) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 55);
        packet.writeShort((short) freq);
        packet.writeFloat(angle);

        packet.sendToPlayer(player);
    }

    public static void sendMapUpdatePacketTo(EntityPlayer player, int mapno, MapData mapdata, TreeSet<FreqCoord> addednodes, TreeSet<FreqCoord> removednodes, TreeSet<FreqCoord> remotes) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 57);
        packet.writeShort((short) addednodes.size());
        for (FreqCoord node : addednodes) {
            packet.writeShort((short) node.x);
            packet.writeShort((short) node.z);
            packet.writeShort((short) (node.freq));
        }

        packet.writeShort((short) removednodes.size());
        for (FreqCoord node : removednodes) {
            packet.writeShort((short) node.x);
            packet.writeShort((short) node.z);
            packet.writeShort((short) (node.freq));
        }

        packet.writeShort((short) remotes.size());
        for (FreqCoord node : remotes) {
            packet.writeInt(node.x);
            packet.writeInt(node.z);
            packet.writeShort((short) (node.freq));
        }

        packet.sendToPlayer(player);
    }

    public static void sendMapInfoTo(EntityPlayer player, int mapno, MapData mapdata) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 56);
        packet.writeShort((short) mapno);
        packet.writeInt(mapdata.xCenter);
        packet.writeInt(mapdata.zCenter);
        packet.writeByte(mapdata.scale);

        packet.sendToPlayer(player);
    }

    public static void sendSpawnREP(EntityREP activeREP) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 59);
        packet.writeBoolean(true);
        packet.writeInt(activeREP.getEntityId());
        packet.writeInt(activeREP.shootingEntity.getEntityId());

        packet.sendToChunk(activeREP.worldObj, (int) activeREP.posX >> 4, (int) activeREP.posZ >> 4);
    }

    public static void sendKillREP(EntityREP entityREP) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 59);
        packet.writeBoolean(false);
        packet.writeInt(entityREP.getEntityId());

        packet.sendToChunk(entityREP.worldObj, (int) entityREP.posX >> 4, (int) entityREP.posZ >> 4);
    }

    public static void sendTrackerUpdatePacketTo(EntityPlayerMP player, EntityWirelessTracker tracker) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 60);
        packet.writeInt(tracker.getEntityId());
        packet.writeShort(tracker.freq);
        packet.writeBoolean(tracker.isAttachedToEntity());
        if (tracker.isAttachedToEntity()) {
            packet.writeInt(tracker.attachedEntity.getEntityId());
            packet.writeFloat(tracker.attachedX);
            packet.writeFloat(tracker.attachedY);
            packet.writeFloat(tracker.attachedZ);
            packet.writeFloat(tracker.attachedYaw);
        } else {
            packet.writeFloat((float) tracker.posX);
            packet.writeFloat((float) tracker.posY);
            packet.writeFloat((float) tracker.posZ);
            packet.writeFloat((float) tracker.motionX);
            packet.writeFloat((float) tracker.motionY);
            packet.writeFloat((float) tracker.motionZ);
            packet.writeShort(tracker.attachmentCounter);
            packet.writeBoolean(tracker.item);
        }

        packet.sendToPlayer(player);
    }

    public static void sendRemoveTrackerTo(EntityPlayerMP player, EntityWirelessTracker tracker) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 61);
        packet.writeBoolean(false);
        packet.writeInt(tracker.getEntityId());

        packet.sendToPlayer(player);
    }

    public static void sendThrowTracker(EntityWirelessTracker tracker, EntityPlayer thrower) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 61);
        packet.writeBoolean(true);
        packet.writeInt(tracker.getEntityId());
        packet.writeInt(thrower.getEntityId());
        packet.writeShort(tracker.freq);

        packet.sendToChunk(thrower.worldObj, (int) thrower.posX >> 4, (int) thrower.posZ >> 4);
    }
}
