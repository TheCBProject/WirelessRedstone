package codechicken.wirelessredstone.addons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "WR-CBE|Addons", dependencies = "required-after:WR-CBE|Core")
public class WirelessRedstoneAddons
{
    public static ItemWirelessTriangulator triangulator;
    public static ItemWirelessRemote remote;
    public static ItemWirelessSniffer sniffer;
    public static Item emptyWirelessMap;
    public static ItemMap wirelessMap;
    public static ItemWirelessTracker tracker;
    public static ItemREP rep;
    public static ItemPrivateSniffer psniffer;
    
    @SidedProxy(clientSide="codechicken.wirelessredstone.addons.WRAddonClientProxy", 
            serverSide="codechicken.wirelessredstone.addons.WRAddonProxy")
    public static WRAddonProxy proxy;
    
    @Instance("WR-CBE|Addons")
    public static WirelessRedstoneAddons instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }
}
