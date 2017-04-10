package codechicken.wirelessredstone.util;

import java.util.TreeSet;

import codechicken.wirelessredstone.api.FreqCoord;

public class WirelessMapNodeStorage
{    
    public void clear()
    {
        nodes.clear();
        devices.clear();
    }
    
    public TreeSet<FreqCoord> nodes = new TreeSet<>();
    public TreeSet<FreqCoord> devices = new TreeSet<>();
}


