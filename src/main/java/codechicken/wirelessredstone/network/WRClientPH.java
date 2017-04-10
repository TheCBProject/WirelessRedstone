package codechicken.wirelessredstone.network;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.entity.EntityREP;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import codechicken.wirelessredstone.util.WirelessMapNodeStorage;
import codechicken.wirelessredstone.api.ClientMapInfo;
import codechicken.wirelessredstone.api.FreqCoord;
import codechicken.wirelessredstone.client.gui.GuiWirelessSniffer;
import codechicken.wirelessredstone.entity.WirelessBolt;
import codechicken.wirelessredstone.manager.RedstoneEther;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WRClientPH implements IClientPacketHandler
{

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        handlePacket(mc.world, mc.player, packet);
    }

    private void handlePacket(WorldClient world, EntityPlayer player, PacketCustom packet) {
        switch (packet.getType()) {
            case 1:
                handleFreqInfoList(packet);
                break;
            case 2:
                handleLastFreqInfo(packet.readUShort(), packet.readUByte());
                break;
            case 3:
                RedstoneEther.get(true).setFrequencyRange(player.getName(), packet.readUShort(), packet.readUShort(), packet.readBoolean());
                break;
            case 4:
                handleFreqInfo(packet);
                break;
            case 7:
                RedstoneEther.client().jamEntity(player, packet.readBoolean());
                break;
            case 8:
                WirelessBolt bolt = new WirelessBolt(world,
                        new Vector3(packet.readFloat(), packet.readFloat(), packet.readFloat()),
                        new Vector3(packet.readFloat(), packet.readFloat(), packet.readFloat()),
                        packet.readLong());

                bolt.defaultFractal();
                bolt.finalizeBolt();
                break;
            case 9:
                RedstoneEther.get(true).setFreqOwner(packet.readShort(), packet.readString());
                break;
            case 10:
                handleFreqOwnerList(packet);
                break;
            case 53:
                processSnifferFreqUpdate(packet);
                break;
            case 54:
                processSnifferEtherCopy(packet);
                break;
            case 55:
                RedstoneEtherAddons.client().setTriangAngle(packet.readUShort(), packet.readFloat());
                break;
            case 56:
                processMapInfo(world, player, packet);
                break;
            case 57:
                processMapUpdate(world, player, packet);
                break;
            case 59:
                if (packet.readBoolean())
                    throwREP(packet.readInt(), packet.readInt(), world, player);
                else
                    world.removeEntityFromWorld(packet.readInt());
                break;
            case 60:
                processTrackerUpdate(packet, world, player);
                break;
            case 61:
                if (packet.readBoolean())
                    throwTracker(world, player, packet.readInt(), packet.readInt(), packet.readUShort());
                else
                    world.removeEntityFromWorld(packet.readInt());
                break;
        }
    }

    private void handleFreqOwnerList(PacketCustom packet) {
        int numFreqs = packet.readUShort();
        for (int i = 0; i < numFreqs; i++)
            RedstoneEther.get(true).setFreqOwner(packet.readShort(), packet.readString());
    }

    private void handleFreqInfoList(PacketCustom packet) {
        int numFreqs = packet.readUShort();
        for (int i = 0; i < numFreqs; i++)
            handleFreqInfo(packet);
    }

    private void handleFreqInfo(PacketCustom packet) {
        int freq = packet.readUShort();
        RedstoneEther.get(true).setFreqColour(freq, packet.readByte());
        RedstoneEther.get(true).setFreqName(freq, packet.readString());
    }

    private void handleLastFreqInfo(int freq, int type) {
        switch (type) {
            case 1:
                RedstoneEther.get(true).setLastPublicFrequency(freq);
                break;
            case 2:
                RedstoneEther.get(true).setLastSharedFrequency(freq);
                break;
        }
    }

    public static void sendSetTileFreq(BlockPos pos, int freq) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 1);
        packet.writePos(pos);
        packet.writeShort(freq);
        packet.sendToServer();
    }

    public static void sendSetFreqInfo(int freq, String name, int colourid) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 4);
        packet.writeShort((short) freq);
        packet.writeString(name);
        packet.writeByte((byte) colourid);
        packet.sendToServer();
    }

    public static void sendDecrementSlot(int slot) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 5);
        packet.writeShort(slot);
        packet.sendToServer();
    }

    public static void sendSetFreqOwner(int freq, String username) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 9);
        packet.writeShort(freq);
        packet.writeString(username);
        packet.sendToServer();
    }

    public static void sendSetItemFreq(int slot, int freq) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 2);
        packet.writeShort(slot);
        packet.writeShort(freq);
        packet.sendToServer();
    }

    private void throwTracker(WorldClient world, EntityPlayer player, int entityID, int throwerID, int freq) {
        Entity thrower = world.getEntityByID(throwerID);
        if (throwerID == player.getEntityId())
            thrower = player;

        if (thrower != null && thrower instanceof EntityLiving) {
            EntityWirelessTracker tracker = new EntityWirelessTracker(world, 0, (EntityLiving) thrower);
            tracker.setEntityId(entityID);
            world.addEntityToWorld(entityID, tracker);
            world.playSound(null, thrower.posX, thrower.posY, thrower.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        }
    }

    private void processTrackerUpdate(PacketCustom packet, WorldClient world, EntityPlayer player) {
        int entityID = packet.readInt();
        int freq = packet.readUShort();
        boolean attached = packet.readBoolean();

        Entity e = world.getEntityByID(entityID);
        if (e != null && e.isDead)
            e = null;

        if (!(e instanceof EntityWirelessTracker)) {
            if (e != null)
                throw new IllegalStateException("EntityID mapped to non tracker");

            e = new EntityWirelessTracker(world, freq);
            e.setEntityId(entityID);
            world.addEntityToWorld(entityID, e);
        }
        EntityWirelessTracker tracker = (EntityWirelessTracker) e;

        if (attached) {
            int attachedEntityID = packet.readInt();

            Entity attachedEntity;
            if (attachedEntityID == player.getEntityId())
                attachedEntity = player;
            else
                attachedEntity = world.getEntityByID(attachedEntityID);

            if (attachedEntity == null) {
                return;
            }

            tracker.attached = true;
            tracker.attachedEntity = attachedEntity;
            tracker.attachedX = packet.readFloat();
            tracker.attachedY = packet.readFloat();
            tracker.attachedZ = packet.readFloat();
            tracker.attachedYaw = packet.readFloat();
        } else {
            tracker.attachedEntity = null;
            tracker.attached = false;

            tracker.posX = packet.readFloat();
            tracker.posY = packet.readFloat();
            tracker.posZ = packet.readFloat();
            tracker.motionX = packet.readFloat();
            tracker.motionY = packet.readFloat();
            tracker.motionZ = packet.readFloat();

            tracker.setPosition(tracker.posX, tracker.posY, tracker.posZ);
            tracker.setVelocity(tracker.motionX, tracker.motionY, tracker.motionZ);

            tracker.attachmentCounter = packet.readUShort();
            tracker.item = packet.readBoolean();
        }
    }

    private void throwREP(int entityID, int throwerID, WorldClient world, EntityPlayer player) {
        Entity thrower = world.getEntityByID(throwerID);
        if (throwerID == player.getEntityId())
            thrower = player;

        if (thrower != null && thrower instanceof EntityLivingBase) {
            EntityREP rep = new EntityREP(world, (EntityLivingBase) thrower);
            rep.setEntityId(entityID);
            world.addEntityToWorld(entityID, rep);
        }
    }

    private static void processSnifferFreqUpdate(PacketCustom packet) {
        GuiScreen currentscreen = Minecraft.getMinecraft().currentScreen;
        if (currentscreen == null || !(currentscreen instanceof GuiWirelessSniffer))
            return;

        GuiWirelessSniffer sniffergui = ((GuiWirelessSniffer) currentscreen);
        sniffergui.setEtherFreq(packet.readUShort(), packet.readBoolean());
    }

    private static void processSnifferEtherCopy(PacketCustom packet) {
        GuiScreen currentscreen = Minecraft.getMinecraft().currentScreen;
        if (currentscreen == null || !(currentscreen instanceof GuiWirelessSniffer))
            return;

        GuiWirelessSniffer sniffergui = ((GuiWirelessSniffer) currentscreen);
        sniffergui.setEtherCopy(packet.readArray(packet.readUShort()));
    }

    private static void processMapUpdate(World world, EntityPlayer player, PacketCustom packet) {
        WirelessMapNodeStorage mapstorage = RedstoneEtherAddons.client().getMapNodes();
        int numaddednodes = packet.readUShort();
        for (int i = 0; i < numaddednodes; i++) {
            FreqCoord node = new FreqCoord(packet.readShort(), -1, packet.readShort(), packet.readUShort());
            mapstorage.nodes.add(node);
        }

        int numremovednodes = packet.readUShort();
        for (int i = 0; i < numremovednodes; i++) {
            FreqCoord node = new FreqCoord(packet.readShort(), -1, packet.readShort(), packet.readUShort());
            mapstorage.nodes.remove(node);
        }

        int numremotes = packet.readUShort();
        mapstorage.devices.clear();
        for (int i = 0; i < numremotes; i++) {
            mapstorage.devices.add(new FreqCoord(packet.readInt(), -1, packet.readInt(), packet.readUShort()));
        }
    }

    private static void processMapInfo(World world, EntityPlayer player, PacketCustom packet) {
        short mapno = packet.readShort();
        int xCenter = packet.readInt();
        int zCenter = packet.readInt();
        byte scale = packet.readByte();
        RedstoneEtherAddons.client().setMPMapInfo(mapno, new ClientMapInfo(xCenter, zCenter, scale));
    }

    public static void sendOpenSniffer() {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 50);
        packet.writeBoolean(true);

        packet.sendToServer();
    }

    public static void sendCloseSniffer() {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 50);
        packet.writeBoolean(false);

        packet.sendToServer();
    }

    public static void sendSetRemote(boolean active) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 51);
        packet.writeBoolean(active);

        packet.sendToServer();
    }

    public static void sendSyncTriang(int freq, boolean required) {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 52);
        packet.writeShort(freq);
        packet.writeBoolean(required);

        packet.sendToServer();
    }

    public static void sendResetMap() {
        PacketCustom packet = new PacketCustom(WirelessRedstone.NET_CHANNEL, 58);

        packet.sendToServer();
    }
}
