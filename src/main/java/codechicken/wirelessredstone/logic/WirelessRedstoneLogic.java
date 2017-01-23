package codechicken.wirelessredstone.logic;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "WR-CBE|Logic", dependencies = "required-after:WR-CBE|Core;required-after:forgemultipartcbe")
public class WirelessRedstoneLogic
{
    public static Item itemwireless;
    
    @SidedProxy(clientSide="codechicken.wirelessredstone.logic.WRLogicClientProxy", 
            serverSide="codechicken.wirelessredstone.logic.WRLogicProxy")
    public static WRLogicProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }
}
