package codechicken.wirelessredstone.api;

public class ClientMapInfo {

    public ClientMapInfo(int xCenter, int zCenter, byte scale) {
        this.xCenter = xCenter;
        this.zCenter = zCenter;
        this.scale = scale;
    }

    public int xCenter;
    public int zCenter;
    public byte scale;
    int dimension;
}
