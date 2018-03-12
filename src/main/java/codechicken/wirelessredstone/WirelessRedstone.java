package codechicken.wirelessredstone;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.gui.SimpleCreativeTab;
import codechicken.lib.internal.ModDescriptionEnhancer;
import codechicken.wirelessredstone.command.CommandFreq;
import codechicken.wirelessredstone.handler.WREventHandler;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.init.PartFactory;
import codechicken.wirelessredstone.manager.SaveManager;
import codechicken.wirelessredstone.proxy.Proxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

import static codechicken.lib.CodeChickenLib.MC_VERSION;
import static codechicken.lib.CodeChickenLib.MC_VERSION_DEP;
import static codechicken.wirelessredstone.WirelessRedstone.*;

/**
 * Created by covers1624 on 23/01/2017.
 */
@Mod (modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, acceptedMinecraftVersions = MC_VERSION_DEP, certificateFingerprint = "f1850c39b2516232a2108a7bd84d1cb5df93b261", updateJSON = UPDATE_URL)
public class WirelessRedstone {

    public static final String NET_CHANNEL = "WRCBE";

    public static final String MOD_ID = "wrcbe";
    public static final String MOD_NAME = "WirelessRedstone-CBE";
    public static final String MOD_VERSION = "${mod_version}";
    public static final String MOD_DEPENDENCIES = CodeChickenLib.MOD_VERSION_DEP + "required-after:forgemultipartcbe";
    static final String UPDATE_URL = "http://chickenbones.net/Files/notification/version.php?query=forge&version=" + MC_VERSION + "&file=WR-CBE";

    @SidedProxy (clientSide = "codechicken.wirelessredstone.proxy.ProxyClient", serverSide = "codechicken.wirelessredstone.proxy.Proxy")
    public static Proxy proxy;

    @Instance (MOD_ID)
    public static WirelessRedstone instance;

    public static CreativeTabs creativeTab = new SimpleCreativeTab("wrcbe", () -> new ItemStack(ModItems.itemWireless, 1, 1));
    public static DamageSource damageBolt;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SaveManager.initConfig(new File(event.getModConfigurationDirectory(), "WirelessRedstone.cfg"));
        proxy.preInit();
        PartFactory.init();
        MinecraftForge.EVENT_BUS.register(new WREventHandler());
        damageBolt = new DamageSource("wrcbe:bolt");
        ModDescriptionEnhancer.registerEnhancement(MOD_ID, "WR-CBE");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandFreq());
    }

}
