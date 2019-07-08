package codechicken.wirelessredstone.manager;

import codechicken.lib.math.MathHelper;
import codechicken.lib.util.CommonUtils;
import codechicken.lib.util.ServerUtils;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.api.FreqCoord;
import codechicken.wirelessredstone.api.WirelessTransmittingDevice;
import codechicken.wirelessredstone.device.Remote;
import codechicken.wirelessredstone.device.Sniffer;
import codechicken.wirelessredstone.entity.EntityREP;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.item.ItemWirelessMap;
import codechicken.wirelessredstone.manager.RedstoneEther.TXNodeInfo;
import codechicken.wirelessredstone.network.WRServerPH;
import codechicken.wirelessredstone.util.WirelessMapNodeStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import java.util.*;
import java.util.Map.Entry;

public class RedstoneEtherServerAddons extends RedstoneEtherAddons {

    private HashMap<String, AddonPlayerInfo> playerInfos = new HashMap<>();
    /**
     * A list of trackers and the players who are 'tracking' them on their clients.
     */
    private HashMap<EntityWirelessTracker, HashSet<EntityPlayerMP>> trackerPlayerMap = new HashMap<>();
    /**
     * Trackers that are attached to players.
     */
    private HashSet<EntityWirelessTracker> playerTrackers = new HashSet<>();

    public void setTriangRequired(EntityPlayer player, int freq, boolean required) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (required) {
            info.triangSet.add(freq);
        } else {
            info.triangSet.remove(freq);
        }
    }

    private AddonPlayerInfo getPlayerInfo(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info == null) {
            this.onLogin(player);
            info = getPlayerInfo(player);
        }
        return info;
    }

    public boolean isRemoteOn(EntityPlayer player, int freq) {
        Remote currentremote = getPlayerInfo(player).remote;
        return currentremote != null && currentremote.getFreq() == freq;
    }

    public int getRemoteFreq(EntityPlayer player) {
        Remote currentremote = getPlayerInfo(player).remote;
        if (currentremote == null) {
            return 0;
        }

        return currentremote.getFreq();
    }

    public void activateRemote(World world, EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.remote != null) {
            if (info.remote.isBeingHeld()) {
                return;
            }
            deactivateRemote(world, player);
        }
        if (RedstoneEther.server().isPlayerJammed(player)) {
            return;
        }
        info.remote = new Remote(player);
        info.remote.metaOn();
        RedstoneEther.server().addTransmittingDevice(info.remote);
    }

    public boolean deactivateRemote(World world, EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.remote == null) {
            return false;
        }

        info.remote.metaOff();
        RedstoneEther.server().removeTransmittingDevice(info.remote);
        info.remote = null;
        return true;
    }

    public void addSniffer(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.sniffer != null) {
            remSniffer(player);
        }
        info.sniffer = new Sniffer(player);
        RedstoneEther.server().addReceivingDevice(info.sniffer);

        byte ethercopy[] = new byte[625];
        for (int freq = 1; freq <= 5000; freq++) {
            int arrayindex = (freq - 1) >> 3;
            int bit = (freq - 1) & 7;
            if (RedstoneEther.server().isFreqOn(freq)) {
                ethercopy[arrayindex] |= 1 << bit;
            }
        }

        WRServerPH.sendEtherCopyTo(player, ethercopy);
    }

    public void remSniffer(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.sniffer == null) {
            return;
        }
        RedstoneEther.server().removeReceivingDevice(info.sniffer);
        info.sniffer = null;
    }

    public void processSMPMaps(World world) {
        RedstoneEther.loadServerWorld(world);
        int dimension = CommonUtils.getDimension(world);
        ArrayList<EntityPlayer> players = ServerUtils.getPlayersInDimension(dimension);

        Map<BlockPos, TXNodeInfo> txnodes = RedstoneEther.server().getTransmittersInDimension(dimension);
        Set<WirelessTransmittingDevice> devices = RedstoneEther.server().getTransmittingDevicesInDimension(dimension);

        for (EntityPlayer player : players) {
            ItemStack helditem = player.getHeldItemMainhand();//TODO Hands?

            if (helditem == null || helditem.getItem() != ModItems.itemWirelessMap || RedstoneEther.server().isPlayerJammed(player)) {
                continue;
            }

            ItemWirelessMap map = (ItemWirelessMap) helditem.getItem();
            MapData mapdata = map.getMapData(helditem, world);

            if (mapdata.dimension != player.dimension) {
                continue;
            }

            WirelessMapNodeStorage mapnodes = getMapNodes(player);
            TreeSet<FreqCoord> oldnodes = mapnodes.nodes;
            int lastdevices = mapnodes.devices.size();

            updatePlayerMapData(player, world, mapdata, txnodes, devices);

            TreeSet<FreqCoord> addednodes = new TreeSet<>(mapnodes.nodes);
            TreeSet<FreqCoord> removednodes = new TreeSet<>();

            if (oldnodes.size() != 0) {
                for (Iterator<FreqCoord> nodeiterator = oldnodes.iterator(); nodeiterator.hasNext(); ) {
                    FreqCoord node = nodeiterator.next();
                    if (!addednodes.remove(node))//remove returns false if the item was not in the set
                    {
                        removednodes.add(node);
                    }
                }
            }

            if (addednodes.size() != 0 || removednodes.size() != 0 || devices.size() != 0 || lastdevices > 0) {
                WRServerPH.sendMapUpdatePacketTo(player, helditem.getItemDamage(), mapdata, addednodes, removednodes, mapnodes.devices);
            }
        }
    }

    private void updatePlayerMapData(EntityPlayer player, World world, MapData mapdata, Map<BlockPos, TXNodeInfo> txnodes, Set<WirelessTransmittingDevice> devices) {
        TreeSet<FreqCoord> mnodes = new TreeSet<>();
        TreeSet<FreqCoord> mdevices = new TreeSet<>();

        int blockwidth = 1 << mapdata.scale;
        int minx = mapdata.xCenter - blockwidth * 64;
        int minz = mapdata.zCenter - blockwidth * 64;
        int maxx = mapdata.xCenter + blockwidth * 64;
        int maxz = mapdata.zCenter + blockwidth * 64;

        for (Entry<BlockPos, TXNodeInfo> entry : txnodes.entrySet()) {
            BlockPos node = entry.getKey();
            TXNodeInfo info = entry.getValue();
            if (info.on && node.getX() > minx && node.getX() < maxx && node.getZ() > minz && node.getZ() < maxz && RedstoneEther.server().canBroadcastOnFrequency(player, info.freq)) {
                mnodes.add(new FreqCoord(node.getX() - mapdata.xCenter, node.getY(), node.getZ() - mapdata.zCenter, info.freq));
            }
        }

        for (Iterator<WirelessTransmittingDevice> iterator = devices.iterator(); iterator.hasNext(); ) {
            WirelessTransmittingDevice device = iterator.next();
            Vector3 pos = device.getTransmitPos();
            if (pos.x > minx && pos.x < maxx && pos.z > minz && pos.z < maxz && RedstoneEther.server().canBroadcastOnFrequency(player, device.getFreq())) {
                mdevices.add(new FreqCoord((int) pos.x, (int) pos.y, (int) pos.z, device.getFreq()));
            }
        }

        WirelessMapNodeStorage mapnodes = getMapNodes(player);

        mapnodes.nodes = mnodes;
        mapnodes.devices = mdevices;
    }

    public void onLogin(EntityPlayer player) {
        playerInfos.put(player.getName(), new AddonPlayerInfo());
    }

    public void onLogout(EntityPlayer player) {
        playerInfos.remove(player.getName());
    }

    public void onDimensionChange(EntityPlayer player) {
        deactivateRemote(player.world, player);
        remSniffer(player);

        playerInfos.put(player.getName(), new AddonPlayerInfo());

        for (Iterator<EntityWirelessTracker> iterator = playerTrackers.iterator(); iterator.hasNext(); ) {
            EntityWirelessTracker tracker = iterator.next();
            if (tracker.attachedPlayerName.equals(player.getName())) {
                tracker.copyToDimension(player.dimension);
                iterator.remove();
            }
        }
    }

    public WirelessMapNodeStorage getMapNodes(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        return info.mapNodes;
    }

    public void updateSMPMapInfo(World world, EntityPlayer player, MapData mapdata, int mapno) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (!info.mapInfoSet.contains(Integer.valueOf(mapno))) {
            WRServerPH.sendMapInfoTo(player, mapno, mapdata);
            info.mapInfoSet.add(mapno);
        }
    }

    public void clearMapNodes(EntityPlayer player) {
        getPlayerInfo(player).mapNodes.clear();
    }

    public void tickTriangs() {
        for (Entry<String, AddonPlayerInfo> entry : playerInfos.entrySet()) {
            EntityPlayer player = ServerUtils.getPlayer(entry.getKey());

            for (Integer freq : entry.getValue().triangSet) {
                double spinto;
                if (!RedstoneEther.server().isFreqOn(freq)) {
                    spinto = -1;
                } else if (isRemoteOn(player, freq)) {
                    spinto = -2;
                } else {
                    Vector3 strengthvec = getBroadcastVector(player, freq);
                    if (strengthvec == null)//in another dimension
                    {
                        spinto = -2;//spin to a random place
                    } else {
                        spinto = (player.rotationYaw + 180) * MathHelper.torad - Math.atan2(-strengthvec.x, strengthvec.z);//spin to the transmitter vec
                    }
                }
                WRServerPH.sendTriangAngleTo(player, freq, (float) spinto);
            }
        }
    }

    public Vector3 getBroadcastVector(EntityPlayer player, int freq) {
        Vector3 vecAmplitude = new Vector3(0, 0, 0);
        Vector3 vecPlayer = new Vector3(player.posX, 0, player.posZ);

        for (Iterator<FreqCoord> iterator = RedstoneEther.server().getActiveTransmittersOnFreq(freq, player.dimension).iterator(); iterator.hasNext(); ) {
            FreqCoord node = iterator.next();

            Vector3 vecTransmitter = new Vector3(node.x + 0.5, 0, node.z + 0.5);
            double distancePow2 = vecTransmitter.subtract(vecPlayer).magSquared();
            vecAmplitude.add(vecTransmitter.multiply(1 / distancePow2));
        }

        for (Iterator<WirelessTransmittingDevice> iterator = RedstoneEther.server().getTransmittingDevicesOnFreq(freq).iterator(); iterator.hasNext(); ) {
            WirelessTransmittingDevice device = iterator.next();

            if (device.getAttachedEntity() == player) {
                return null;
            }

            if (device.getDimension() != player.dimension) {
                continue;
            }

            Vector3 vecTransmitter = device.getTransmitPos();
            vecTransmitter.y = 0;
            double distancePow2 = vecTransmitter.subtract(vecPlayer).magSquared();
            vecAmplitude.add(vecTransmitter.multiply(1 / distancePow2));
        }

        if (vecAmplitude.isZero()) {
            return null;
        }

        return vecAmplitude;
    }

    public void addTracker(EntityWirelessTracker tracker) {
        trackerPlayerMap.put(tracker, new HashSet<>());

        if (tracker.attachedPlayerName != null) {
            playerTrackers.add(tracker);
        }
    }

    public void removeTracker(EntityWirelessTracker tracker) {
        HashSet<EntityPlayerMP> trackedPlayers = trackerPlayerMap.get(tracker);
        if (trackedPlayers != null) {
            for (EntityPlayerMP player : trackedPlayers) {
                WRServerPH.sendRemoveTrackerTo(player, tracker);
            }
        }
        trackerPlayerMap.remove(tracker);

        if (tracker.attachedInOtherDimension()) {
            //removed as player has left dimension...
            //keep this tracker in the playerTrackers list, to be notified of the dimension change
        } else {
            playerTrackers.remove(tracker);
        }
    }

    public void updateTracker(EntityWirelessTracker tracker) {
        HashSet<EntityPlayerMP> trackedPlayers = trackerPlayerMap.get(tracker);
        if (trackedPlayers == null) {
            trackerPlayerMap.put(tracker, trackedPlayers = new HashSet<>());
        }

        for (EntityPlayerMP player : trackedPlayers) {
            WRServerPH.sendTrackerUpdatePacketTo(player, tracker);
        }

        if (tracker.attachedPlayerName != null) {
            playerTrackers.add(tracker);
        } else {
            playerTrackers.remove(tracker);
        }
    }

    int trackerTicks = 0;

    public void processTrackers() {
        trackerTicks++;
        HashSet<EntityPlayer> playerEntities = new HashSet<>(ServerUtils.getPlayers());

        boolean updateFree = trackerTicks % 5 == 0;
        boolean updateAttached = trackerTicks % 100 == 0;

        for (Iterator<Entry<EntityWirelessTracker, HashSet<EntityPlayerMP>>> iterator = trackerPlayerMap.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<EntityWirelessTracker, HashSet<EntityPlayerMP>> entry = iterator.next();

            HashSet<EntityPlayerMP> trackedPlayers = entry.getValue();
            HashSet<EntityPlayerMP> playersToTrack = new HashSet<>();

            EntityWirelessTracker tracker = entry.getKey();
            ChunkPos chunk = new ChunkPos(tracker.chunkCoordX, tracker.chunkCoordZ);

            for (EntityPlayer entityPlayer : playerEntities) {
                EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
                if (tracker.isDead) {
                    WRServerPH.sendRemoveTrackerTo(player, tracker);
                } else if (tracker.getDimension() == player.dimension && /*TODO*/!ServerUtils.isPlayerLoadingChunk(player, chunk) && !tracker.attachedToLogout())//perform update, add to list
                {
                    playersToTrack.add(player);
                    if (!trackedPlayers.contains(player) || (tracker.isAttachedToEntity() && updateAttached) || (!tracker.isAttachedToEntity() && updateFree)) {
                        WRServerPH.sendTrackerUpdatePacketTo(player, tracker);
                    }
                } else if (trackedPlayers.contains(player))//no longer in listening range
                {
                    WRServerPH.sendRemoveTrackerTo(player, tracker);
                }
            }

            if (tracker.isDead) {
                iterator.remove();
                continue;
            }

            trackedPlayers.clear();
            trackedPlayers.addAll(playersToTrack);
        }
    }

    public boolean detonateREP(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.activeREP == null) {
            return false;
        } else if (info.activeREP.isDead) {
            info.activeREP = null;
            return false;
        } else {
            info.activeREP.detonate();
            info.activeREP.setDead();
            return true;
        }
    }

    public void invalidateREP(EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info != null) {
            info.activeREP = null;
        }
    }

    public void updateREPTimeouts() {
        for (Entry<String, AddonPlayerInfo> entry : playerInfos.entrySet()) {
            AddonPlayerInfo info = entry.getValue();
            if (info.REPThrowTimeout > 0) {
                info.REPThrowTimeout--;
            }
        }
    }

    public void throwREP(ItemStack itemstack, World world, EntityPlayer player) {
        AddonPlayerInfo info = getPlayerInfo(player);
        if (info.REPThrowTimeout > 0) {
            return;
        }

        if (!player.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        EntityREP activeREP = new EntityREP(world, player);
        world.spawnEntity(activeREP);
        WRServerPH.sendSpawnREP(activeREP);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        info.activeREP = activeREP;
        info.REPThrowTimeout = 40;
    }
}
