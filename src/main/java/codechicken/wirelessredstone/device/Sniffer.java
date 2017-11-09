package codechicken.wirelessredstone.device;

import codechicken.wirelessredstone.api.WirelessReceivingDevice;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.network.WRServerPH;
import net.minecraft.entity.player.EntityPlayer;

public class Sniffer implements WirelessReceivingDevice {

    public Sniffer(EntityPlayer player) {
        owner = player;
    }

    public void updateDevice(int freq, boolean on) {
        if (RedstoneEther.get(false).canBroadcastOnFrequency(owner, freq)) {
            WRServerPH.sendUpdateSnifferTo(owner, freq, on);
        }
    }

    EntityPlayer owner;
}
