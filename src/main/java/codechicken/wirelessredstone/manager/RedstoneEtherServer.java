package codechicken.wirelessredstone.manager;

import codechicken.lib.util.CommonUtils;
import codechicken.lib.util.ServerUtils;
import codechicken.lib.vec.Vector3;
import codechicken.wirelessredstone.api.*;
import codechicken.wirelessredstone.network.WRServerPH;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.Map.Entry;

public class RedstoneEtherServer extends RedstoneEther {

    public RedstoneEtherServer() {
        super(false);
    }

    public void init(World world) {
        super.init(world);

        SaveManager.resetWorld();
        SaveManager.loadFreqInfo();
        SaveManager.loadDimensionHash();

        publicfrequencyend = SaveManager.generalProp.getProperty("PublicFrequencies", 1000);
        sharedfrequencyend = SaveManager.generalProp.getProperty("SharedFrequencies", 5000);
        numprivatefreqs = SaveManager.generalProp.getProperty("PrivateFrequencies", 50);
    }

    @Override
    protected void addEther(World world, int dimension) {
        if (ethers.get(dimension) != null) {
            return;
        }

        super.addEther(world, dimension);

        SaveManager.reloadSave(world);
        SaveManager.getInstance(dimension).loadEther();
    }

    public void remEther(World world, int dimension) {
        if (ethers.get(dimension) == null) {
            return;
        }

        super.remEther(world, dimension);

        SaveManager.unloadSave(dimension);
    }

    public void saveEther(World world) {
        int dimension = CommonUtils.getDimension(world);
        if (!ethers.containsKey(dimension)) {
            return;
        }

        for (RedstoneEtherFrequency freq : ethers.get(dimension).freqsToSave) {
            freq.saveFreq(dimension);
        }

        ethers.get(dimension).freqsToSave.clear();
        SaveManager.getInstance(dimension).removeTrailingSectors();
        SaveManager.saveDimensionHash();
    }

    public void verifyChunkTransmitters(World world, int chunkx, int chunkz) {
        int dimension = CommonUtils.getDimension(world);
        DimensionalEtherHash ether = ethers.get(dimension);
        int blockxmin = chunkx * 16;
        int blockxmax = blockxmin + 15;
        int blockzmin = chunkz * 16;
        int blockzmax = blockzmin + 15;

        ArrayList<BlockPos> transmittingblocks = new ArrayList<>(ether.transmittingblocks.keySet());

        for (BlockPos node : transmittingblocks) {
            if (node.getX() >= blockxmin && node.getX() <= blockxmax && node.getZ() >= blockzmin && node.getZ() <= blockzmax) {
                TileEntity tile = RedstoneEther.getTile(world, node);
                int freq = ether.transmittingblocks.get(node).freq;
                if (!(tile instanceof ITileWireless) || ((ITileWireless) tile).getFreq() != freq) {
                    remTransmitter(world, node, freq);
                    System.out.println("Removed Badly Synced node at:" + node.getX() + "," + node.getY() + "," + node.getZ() + " on " + freq + " in dim" + dimension);
                }
            }
        }
    }

    public void setTransmitter(World world, BlockPos node, int freq, boolean on) {
        if (freq == 0) {
            return;
        }

        int dimension = CommonUtils.getDimension(world);

        if (isNodeInAOEofJammer(node, dimension)) {
            jamNodeSometime(world, node, dimension, freq);
        }
        TXNodeInfo info = ethers.get(dimension).transmittingblocks.get(node);
        if (info == null) {
            ethers.get(dimension).transmittingblocks.put(node, new TXNodeInfo(freq, on));
        } else {
            info.on = on;
        }
        freqarray[freq].setTransmitter(world, node, dimension, on);
    }

    public void remTransmitter(World world, BlockPos node, int freq) {
        if (freq == 0) {
            return;
        }

        int dimension = CommonUtils.getDimension(world);

        ethers.get(dimension).jammednodes.remove(node);
        ethers.get(dimension).transmittingblocks.remove(node);
        freqarray[freq].remTransmitter(world, node, dimension);
    }

    public void addReceiver(World world, BlockPos node, int freq) {
        if (freq == 0) {
            return;
        }

        int dimension = CommonUtils.getDimension(world);

        if (isNodeInAOEofJammer(node, dimension)) {
            jamNodeSometime(world, node, dimension, freq);
        }
        ethers.get(dimension).recievingblocks.put(node, freq);
        freqarray[freq].addReceiver(world, node, dimension);
    }

    public void remReceiver(World world, BlockPos node, int freq) {
        if (freq == 0) {
            return;
        }

        int dimension = CommonUtils.getDimension(world);

        ethers.get(dimension).jammednodes.remove(node);
        ethers.get(dimension).recievingblocks.remove(node);
        freqarray[freq].remReceiver(world, node, dimension);
    }

    public void addJammer(World world, BlockPos jammer) {
        int dimension = CommonUtils.getDimension(world);

        ethers.get(dimension).jammerset.add(jammer);
        jamNodesInAOEOfJammer(world, jammer, dimension);
    }

    public void remJammer(World world, BlockPos jammer) {
        ethers.get(CommonUtils.getDimension(world)).jammerset.remove(jammer);
    }

    public boolean isNodeJammed(World world, int x, int y, int z) {
        Integer timeout = ethers.get(CommonUtils.getDimension(world)).jammednodes.get(new BlockPos(x, y, z));
        return timeout != null && timeout > 0;
    }

    public boolean isNodeInAOEofJammer(BlockPos node, int dimension) {
        for (BlockPos jammer : ethers.get(dimension).jammerset) {
            if (pythagorasPow2(jammer, node) < jammerrangePow2) {
                return true;
            }
        }
        return false;
    }

    public boolean isPointInAOEofJammer(Vector3 point, int dimension) {
        for (BlockPos jammer : ethers.get(dimension).jammerset) {
            if (pythagorasPow2(jammer, point) < jammerrangePow2) {
                return true;
            }
        }
        return false;
    }

    public BlockPos getClosestJammer(BlockPos node, int dimension) {
        BlockPos closestjammer = null;
        double closestdist = jammerrangePow2;
        for (BlockPos jammer : ethers.get(dimension).jammerset) {
            double distance = pythagorasPow2(jammer, node);
            if (distance < closestdist) {
                closestjammer = jammer;
                closestdist = distance;
            }
        }
        return closestjammer;
    }

    public BlockPos getClosestJammer(Vector3 point, int dimension) {
        BlockPos closestjammer = null;
        double closestdist = jammerrangePow2;
        for (BlockPos jammer : ethers.get(dimension).jammerset) {
            double distance = pythagorasPow2(jammer, point);
            if (distance < closestdist) {
                closestjammer = jammer;
                closestdist = distance;
            }
        }
        return closestjammer;
    }

    public void jamNodeSometime(World world, BlockPos node, int dimension, int freq) {
        ethers.get(dimension).jammednodes.put(node, -world.rand.nextInt(jammerblockwait));
    }

    public void jamEntitySometime(EntityLivingBase entity) {
        jammedentities.put(entity, -entity.world.rand.nextInt(jammerentitywait));
    }

    public void jamNode(World world, BlockPos node, int dimension, int freq) {
        ethers.get(dimension).jammednodes.put(node, getRandomTimeout(world.rand));

        freqarray[freq].remTransmitter(world, node, dimension);
        freqarray[freq].remReceiver(world, node, dimension);
    }

    public void jamNode(World world, BlockPos pos, int freq) {
        if (freq == 0) {
            return;
        }

        jamNode(world, pos, CommonUtils.getDimension(world), freq);
    }

    @Override
    public void jamEntity(EntityLivingBase entity, boolean jam) {
        if (jam)//iterator.remove will be used to unjam entities. We only need to send the packet.
        {
            jammedentities.put(entity, getRandomTimeout(entity.world.rand));
        }
        if (entity instanceof EntityPlayer) {
            WRServerPH.sendJamPlayerPacketTo((EntityPlayer) entity, jam);
        }
    }

    public void jamNodesInAOEOfJammer(World world, BlockPos jammer, int dimension) {
        for (int freq = 1; freq <= numfreqs; freq++) {
            TreeMap<BlockPos, Boolean> transmittermap = freqarray[freq].getTransmitters(dimension);
            for (BlockPos node : transmittermap.keySet()) {
                if (pythagorasPow2(node, jammer) < jammerrangePow2) {
                    jamNodeSometime(world, node, dimension, freq);
                }
            }

            TreeSet<BlockPos> receiverset = freqarray[freq].getReceivers(dimension);
            for (BlockPos node : receiverset) {
                if (pythagorasPow2(node, jammer) < jammerrangePow2) {
                    jamNodeSometime(world, node, dimension, freq);
                }
            }
        }
    }

    public void unjamTile(World world, int x, int y, int z) {
        BlockPos node = new BlockPos(x, y, z);
        int dimension = CommonUtils.getDimension(world);

        Integer timeout = ethers.get(dimension).jammednodes.remove(node);

        if (timeout != null && timeout >= 0)//tile was jammed
        {
            ITileWireless tile = (ITileWireless) getTile(world, node);
            tile.unjamTile();
        }
    }

    public void saveJammedFrequencies(String username) {
        username = username.toLowerCase();
        String jammedfreqs = getJammedFrequencies(username);

        if (jammedfreqs.equals("" + (sharedfrequencyend + 1) + "-" + numfreqs)) {
            SaveManager.generalProp.removeProperty(username + ".jammedFreqs");
        } else {
            SaveManager.generalProp.setProperty(username + ".jammedFreqs", jammedfreqs);
        }
    }

    public void loadJammedFrequencies(String jammedString, String username) {
        String[] freqranges = jammedString.split(",");
        for (String freqrange : freqranges) {
            String[] currentrange = freqrange.split("-");
            int startfreq;
            int endfreq;
            if (currentrange.length == 1) {
                try {
                    startfreq = endfreq = Integer.parseInt(currentrange[0]);
                } catch (NumberFormatException numberformatexception) {
                    continue;
                }
            } else {
                try {
                    startfreq = Integer.parseInt(currentrange[0]);
                    endfreq = Integer.parseInt(currentrange[1]);
                } catch (NumberFormatException numberformatexception1) {
                    continue;
                }
            }

            setFrequencyRange(username, startfreq, endfreq, true);
        }
    }

    @Override
    protected void loadJammedFrequencies(String username) {
        String openstring = SaveManager.generalProp.getProperty(username + ".jammedFreqs");
        if (openstring == null) {
            jamDefaultRange(username);
        } else {
            loadJammedFrequencies(openstring, username);
        }
    }

    public void setFrequencyRangeCommand(String username, int startfreq, int endfreq, boolean flag) {
        setFrequencyRange(username, startfreq, endfreq, flag);
        saveJammedFrequencies(username);
    }

    public void jamAllFrequencies(String username) {
        setFrequencyRange(username, 1, numfreqs, true);
    }

    public void jamDefaultRange(String username) {
        setFrequencyRange(username, 1, numfreqs, false);
        setFrequencyRange(username, sharedfrequencyend + 1, numfreqs, true);
    }

    public void setFreqClean(int freq, int dimension) {
        freqarray[freq].setClean(dimension);
    }

    public void resetPlayer(EntityPlayer player) {
        WRServerPH.sendPublicFrequencyTo(player, publicfrequencyend);
        WRServerPH.sendSharedFrequencyTo(player, sharedfrequencyend);

        String openstring = SaveManager.generalProp.getProperty(player.getName() + ".jammedFreqs");
        if (openstring == null) {
            jamDefaultRange(player.getName());
        } else {
            loadJammedFrequencies(openstring, player.getName());
        }

        sendFreqInfoTo(player);
        sendPrivateFreqsTo(player);
    }

    public void removePlayer(EntityPlayer player) {
        playerJammedMap.remove(player.getName());
    }

    private void sendFreqInfoTo(EntityPlayer player) {
        ArrayList<Integer> freqsWithInfo = new ArrayList<>();
        for (int freq = 1; freq <= numfreqs; freq++) {
            if (!freqarray[freq].getName().equals("") || freqarray[freq].getColourId() != -1) {
                freqsWithInfo.add(freq);
            }
        }

        WRServerPH.sendFreqInfoTo(player, freqsWithInfo);
    }

    private void sendPrivateFreqsTo(EntityPlayer player) {
        ArrayList<Integer> freqsWithOwners = new ArrayList<>();
        for (int freq = 1; freq <= numfreqs; freq++) {
            if (isFreqPrivate(freq)) {
                freqsWithOwners.add(freq);
            }
        }

        WRServerPH.sendFreqOwnerTo(player, freqsWithOwners);
    }

    public TreeMap<Integer, Integer> getLoadedFrequencies() {
        TreeMap<Integer, Integer> treemap = new TreeMap<>();
        for (int freq = 1; freq <= numfreqs; freq++) {
            if (freqarray[freq].nodeCount() != 0) {
                treemap.put(freq, freqarray[freq].getActiveTransmitters());
            }
        }

        return treemap;
    }

    public Map<BlockPos, Boolean> getTransmittersOnFreq(int freq, int dimension) {
        return Collections.unmodifiableMap(freqarray[freq].getTransmitters(dimension));
    }

    public Collection<BlockPos> getReceiversOnFreq(int freq, int dimension) {
        return Collections.unmodifiableCollection(freqarray[freq].getReceivers(dimension));
    }

    public Map<BlockPos, TXNodeInfo> getTransmittersInDimension(int dimension) {
        return Collections.unmodifiableMap(ethers.get(dimension).transmittingblocks);
    }

    public Set<WirelessTransmittingDevice> getTransmittingDevicesInDimension(int dimension) {
        return Collections.unmodifiableSet(ethers.get(dimension).transmittingdevices);
    }

    public ArrayList<FreqCoord> getActiveTransmittersOnFreq(int freq, int dimension) {
        ArrayList<FreqCoord> txnodes = new ArrayList<>();
        freqarray[freq].putActiveTransmittersInList(dimension, txnodes);
        return txnodes;
    }

    public TreeSet<BlockPos> getJammers(int dimension) {
        return ethers.get(dimension).jammerset;
    }

    public TreeMap<BlockPos, Integer> getJammedNodes(int dimension) {
        return ethers.get(dimension).jammednodes;
    }

    public TreeSet<BlockPos> getNodesInRangeofPoint(int dimension, Vector3 point, float range, boolean includejammed) {
        TreeSet<BlockPos> nodes = new TreeSet<>();
        float rangePow2 = range * range;
        for (int freq = 1; freq <= numfreqs; freq++) {
            TreeMap<BlockPos, Boolean> transmittermap = freqarray[freq].getTransmitters(dimension);
            for (BlockPos node : transmittermap.keySet()) {
                if (pythagorasPow2(node, point) < rangePow2) {
                    nodes.add(node);
                }
            }

            TreeSet<BlockPos> receiverset = freqarray[freq].getReceivers(dimension);
            for (BlockPos node : receiverset) {
                if (pythagorasPow2(node, point) < rangePow2) {
                    nodes.add(node);
                }
            }
        }

        if (includejammed) {
            for (BlockPos node : ethers.get(dimension).jammednodes.keySet()) {
                if (pythagorasPow2(node, point) < rangePow2) {
                    nodes.add(node);
                }
            }
        }

        return nodes;
    }

    public TreeSet<BlockPos> getNodesInRangeofNode(int dimension, BlockPos block, float range, boolean includejammed) {
        TreeSet<BlockPos> nodes = new TreeSet<>();
        float rangePow2 = range * range;
        for (int freq = 1; freq <= numfreqs; freq++) {
            TreeMap<BlockPos, Boolean> transmittermap = freqarray[freq].getTransmitters(dimension);
            for (BlockPos node : transmittermap.keySet()) {
                if (pythagorasPow2(node, block) < rangePow2) {
                    nodes.add(node);
                }
            }

            TreeSet<BlockPos> receiverset = freqarray[freq].getReceivers(dimension);
            for (BlockPos node : receiverset) {
                if (pythagorasPow2(node, block) < rangePow2) {
                    nodes.add(node);
                }
            }
        }

        if (includejammed) {
            for (BlockPos node : ethers.get(dimension).jammednodes.keySet()) {
                if (pythagorasPow2(node, block) < rangePow2) {
                    nodes.add(node);
                }
            }
        }

        return nodes;
    }

    public void updateReceivingDevices(int freq, boolean on) {
        for (WirelessReceivingDevice receivingdevice : receivingdevices) {
            receivingdevice.updateDevice(freq, on);
        }
    }

    public List<WirelessTransmittingDevice> getTransmittingDevicesOnFreq(int freq) {
        return Collections.unmodifiableList(freqarray[freq].getTransmittingDevices());
    }

    public void addTransmittingDevice(WirelessTransmittingDevice device) {
        ethers.get(device.getDimension()).transmittingdevices.add(device);
        freqarray[device.getFreq()].addTransmittingDevice(device);
    }

    public void removeTransmittingDevice(WirelessTransmittingDevice device) {
        ethers.get(device.getDimension()).transmittingdevices.remove(device);
        freqarray[device.getFreq()].removeTransmittingDevice(device);
    }

    public void addReceivingDevice(WirelessReceivingDevice device) {
        receivingdevices.add(device);
    }

    public void removeReceivingDevice(WirelessReceivingDevice device) {
        receivingdevices.remove(device);
    }

    public void setDimensionTransmitterCount(int freq, int dimension, int count) {
        freqarray[freq].setActiveTransmittersInDim(dimension, count);
    }

    public void addFreqToSave(RedstoneEtherFrequency freq, int dimension) {
        ethers.get(dimension).freqsToSave.add(freq);
    }

    public void tick(World world) {
        updateJammedNodes(world);
        randomJamTest(world);
        updateJammedEntities(world);
        entityJamTest(world);
        unloadJammedMap();
    }

    private void unloadJammedMap() {
        for (Iterator<String> iterator = playerJammedMap.keySet().iterator(); iterator.hasNext(); ) {
            String username = iterator.next();
            if (ServerUtils.getPlayer(username) == null) {
                saveJammedFrequencies(username);
                iterator.remove();
            }
        }
    }

    private void updateJammedNodes(World world) {
        int dimension = CommonUtils.getDimension(world);
        DimensionalEtherHash e = ethers.get(dimension);
        if (e != null) {
            for (Iterator<BlockPos> iterator = e.jammednodes.keySet().iterator(); iterator.hasNext(); ) {
                BlockPos node = iterator.next();
                int inactivetime = e.jammednodes.get(node);
                inactivetime--;

                if (inactivetime == 0 || inactivetime < 0 && inactivetime % jammerrandom == 0) {
                    ITileWireless tile = (ITileWireless) getTile(world, node);
                    if (tile == null) {
                        iterator.remove();
                        continue;
                    }

                    BlockPos jammer = getClosestJammer(node, dimension);
                    ITileJammer jammertile = jammer == null ? null : (ITileJammer) getTile(world, jammer);
                    if (jammertile == null) {
                        iterator.remove();
                        tile.unjamTile();
                        continue;
                    }
                    jammertile.jamTile(tile);
                }

                if (inactivetime == 0)//so the node doesn't think it's unjammed
                {
                    inactivetime = jammertimeout;
                }

                e.jammednodes.put(node, inactivetime);
            }
        }
    }

    private void randomJamTest(World world) {
        if (world.getTotalWorldTime() % 600 != 0)//30 seconds
        {
            return;
        }

        for (Entry<Integer, DimensionalEtherHash> entry : ethers.entrySet()) {
            if (entry.getValue().jammerset != null) {
                for (BlockPos blockPos : entry.getValue().jammerset) {
                    jamNodesInAOEOfJammer(world, blockPos, entry.getKey());
                }
            }
        }
    }

    private void updateJammedEntities(World world) {
        int dimension = CommonUtils.getDimension(world);
        for (Iterator<EntityLivingBase> iterator = jammedentities.keySet().iterator(); iterator.hasNext(); ) {
            EntityLivingBase entity = iterator.next();
            int inactivetime = jammedentities.get(entity);
            inactivetime--;

            if (entity == null || entity.isDead)//logged out or killed
            {
                iterator.remove();
                continue;
            }

            if (inactivetime == 0//time for unjam or rejam
                    || (inactivetime < 0 && inactivetime % jammerentitywait == 0)//time to jam from the sometime
                    || (inactivetime > 0 && inactivetime % jammerentityretry == 0))//send another bolt after the retry time
            {
                BlockPos jammer = getClosestJammer(Vector3.fromEntity(entity), dimension);
                ITileJammer jammertile = jammer == null ? null : (ITileJammer) getTile(world, jammer);
                if (jammertile == null) {
                    if (inactivetime <= 0)//not a rejam test
                    {
                        iterator.remove();
                        jamEntity(entity, false);
                        continue;
                    }
                } else {
                    jammertile.jamEntity(entity);
                }
            }

            if (inactivetime == 0)//so the node doesn't think it's unjammed
            {
                inactivetime = jammertimeout;
            }

            jammedentities.put(entity, inactivetime);
        }
    }

    private void entityJamTest(World world) {
        if (world.getTotalWorldTime() % 10 != 0) {
            return;
        }

        int dimension = CommonUtils.getDimension(world);
        DimensionalEtherHash e = ethers.get(dimension);
        if (e != null) {
            for (BlockPos jammer : e.jammerset) {
                List<Entity> entitiesinrange = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(jammer.getX() - 9.5, jammer.getY() - 9.5, jammer.getZ() - 9.5, jammer.getX() + 10.5, jammer.getY() + 10.5, jammer.getZ() + 10.5));
                for (Entity entity : entitiesinrange) {
                    if (!(entity instanceof EntityLivingBase)) {
                        continue;
                    }

                    if (entity instanceof EntityPlayer) {
                        if (isPlayerJammed((EntityPlayer) entity)) {
                            continue;
                        }
                    }

                    jamEntitySometime((EntityLivingBase) entity);
                }
            }
        }
    }

    public void unload() {
        SaveManager.unloadAll();
    }

    @Override
    public void setFreq(ITileWireless tile, int freq) {
        tile.setFreq(freq);
    }
}
