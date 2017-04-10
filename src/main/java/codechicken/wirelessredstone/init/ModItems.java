package codechicken.wirelessredstone.init;

import codechicken.lib.item.ItemMultiType;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by covers1624 on 24/01/2017.
 */
public class ModItems {

    public static Item itemWireless;

    public static ItemMultiType itemMaterial;

    public static ItemWirelessTriangulator itemTriangulator;
    public static ItemWirelessRemote itemRemote;
    public static ItemWirelessSniffer itemSniffer;
    public static Item itemEmptyWirelessMap;
    public static ItemMap itemWirelessMap;
    public static ItemWirelessTracker itemTracker;
    public static ItemREP itemRep;
    public static ItemPrivateSniffer itemPrivateSniffer;

    public static ItemStack obsidianStick;
    public static ItemStack stoneBowl;
    public static ItemStack retherPearl;
    public static ItemStack wirelessTransceiver;
    public static ItemStack blazeTransceiver;
    public static ItemStack receiverDish;
    public static ItemStack blazeReceiverDish;


    public static void init() {
        itemWireless = new ItemWirelessPart().setCreativeTab(WirelessRedstone.creativeTab);
        GameRegistry.register(ModItems.itemWireless.setRegistryName("wireless_logic"));

        itemMaterial = new ItemMultiType(WirelessRedstone.creativeTab, "material").setUnlocalizedName("wrcbe:material");
        GameRegistry.register(itemMaterial);

        obsidianStick = itemMaterial.registerItem("obsidian_stick");
        stoneBowl = itemMaterial.registerItem("stone_bowl");
        retherPearl = itemMaterial.registerItem("rether_pearl");
        wirelessTransceiver = itemMaterial.registerItem("wireless_transceiver");
        blazeTransceiver = itemMaterial.registerItem("blaze_transceiver");
        receiverDish = itemMaterial.registerItem("receiver_dish");
        blazeReceiverDish = itemMaterial.registerItem("blaze_receiver_dish");

        OreDictionary.registerOre("obsidianRod", obsidianStick);
        OreDictionary.registerOre("stoneBowl", stoneBowl);

        itemTriangulator = new ItemWirelessTriangulator();
        GameRegistry.register(itemTriangulator.setRegistryName("triangulator"));
        itemRemote = new ItemWirelessRemote();
        GameRegistry.register(itemRemote.setRegistryName("remote"));
        itemSniffer = new ItemWirelessSniffer();
        GameRegistry.register(itemSniffer.setRegistryName("sniffer"));
        itemEmptyWirelessMap = new ItemEmptyWirelessMap();
        GameRegistry.register(itemEmptyWirelessMap.setRegistryName("empty_map"));
        itemWirelessMap = new ItemWirelessMap();
        GameRegistry.register(itemWirelessMap.setRegistryName("map"));
        itemTracker = new ItemWirelessTracker();
        GameRegistry.register(itemTracker.setRegistryName("tracker"));
        itemRep = new ItemREP();
        GameRegistry.register(itemRep.setRegistryName("rep"));
        itemPrivateSniffer = new ItemPrivateSniffer();
        GameRegistry.register(itemPrivateSniffer.setRegistryName("p_sniffer"));
    }


}
