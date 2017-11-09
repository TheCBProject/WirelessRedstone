package codechicken.wirelessredstone.util;

import codechicken.wirelessredstone.api.FreqCoord;

import java.util.TreeSet;

public class WirelessMapNodeStorage {

    public void clear() {
        nodes.clear();
        devices.clear();
    }

    public TreeSet<FreqCoord> nodes = new TreeSet<>();
    public TreeSet<FreqCoord> devices = new TreeSet<>();
}


