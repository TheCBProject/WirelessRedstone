package codechicken.wirelessredstone.api;

import codechicken.lib.vec.Vector3;
import net.minecraft.entity.Entity;

public interface ITileJammer {

    void jamTile(ITileWireless tile);

    void jamEntity(Entity entity);

    Vector3 getFocalPoint();
}
