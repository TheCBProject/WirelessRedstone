package codechicken.wirelessredstone.proxy;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.util.CommonUtils;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.network.WRServerPH;
import codechicken.wirelessredstone.entity.WirelessBolt;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.init.ModRecipes;
import codechicken.wirelessredstone.manager.SaveManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static codechicken.wirelessredstone.WirelessRedstone.NET_CHANNEL;

/**
 * Created by covers1624 on 23/01/2017.
 */
public class Proxy {

    public void preInit() {
        ModItems.init();
        PacketCustom.assignHandler(NET_CHANNEL, new WRServerPH());
        ConfigTag coreconfig = SaveManager.config().getTag("core").useBraces().setPosition(10);
        WirelessBolt.init(coreconfig);
        EntityRegistry.registerModEntity(new ResourceLocation("wrcbe:tracker"), EntityWirelessTracker.class, "tracker", 0, WirelessRedstone.instance, 64, 1, true);
    }

    public void init(){
        ModRecipes.init();
    }

    public void postInit(){

    }

    public void openItemWirelessGui(EntityPlayer entityplayer) {
    }

    public void openTileWirelessGui(EntityPlayer entityplayer, ITileWireless tileRPWireless) {
    }

    public void openPSnifferGui(EntityPlayer player)
    {
    }

    public void openSnifferGui(EntityPlayer player)
    {
    }

}
