package codechicken.wirelessredstone;

import codechicken.lib.gui.SimpleCreativeTab;
import codechicken.wirelessredstone.command.CommandFreq;
import codechicken.wirelessredstone.handler.WREventHandler;
import codechicken.wirelessredstone.init.PartFactory;
import codechicken.wirelessredstone.manager.SaveManager;
import codechicken.wirelessredstone.proxy.CommonProxy;
import codechicken.wirelessredstone.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
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

/**
 * Created by covers1624 on 23/01/2017.
 */
@Mod (modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class WirelessRedstone {

    public static final String NET_CHANNEL = "WRCBE";

    @SidedProxy (clientSide = "codechicken.wirelessredstone.proxy.ClientProxy", serverSide = "codechicken.wirelessredstone.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Reference.MOD_ID)
    public static WirelessRedstone instance;

    public static CreativeTabs creativeTab = new SimpleCreativeTab("wrcbe", "wrcbe:wirelessLogic", 1);
    public static DamageSource damageBolt;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SaveManager.initConfig(new File(event.getModConfigurationDirectory(), "WirelessRedstone.cfg"));
        proxy.preInit();
        PartFactory.init();
        MinecraftForge.EVENT_BUS.register(new WREventHandler());
        damageBolt = new DamageSource("wrcbe:bolt");
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
