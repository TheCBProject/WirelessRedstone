package codechicken.wirelessredstone.api;

import codechicken.lib.vec.Vector3;
import net.minecraft.entity.EntityLivingBase;

public interface WirelessTransmittingDevice
{
    public Vector3 getTransmitPos();
    public int getDimension();
    public int getFreq();
    EntityLivingBase getAttachedEntity();
}
