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

    public static ItemWirelessTriangulator triangulator;
    public static ItemWirelessRemote remote;
    public static ItemWirelessSniffer sniffer;
    public static Item emptyWirelessMap;
    public static ItemMap wirelessMap;
    public static ItemWirelessTracker tracker;
    public static ItemREP itemRep;
    public static ItemPrivateSniffer pSniffer;

    public static ItemStack obsidianStick;
    public static ItemStack stoneBowl;
    public static ItemStack retherPearl;
    public static ItemStack wirelessTransceiver;
    public static ItemStack blazeTransceiver;
    public static ItemStack receiverDish;
    public static ItemStack blazeReceiverDish;


    public static void init() {
        itemWireless = new ItemWirelessPart().setCreativeTab(WirelessRedstone.creativeTab);
        GameRegistry.register(ModItems.itemWireless.setRegistryName("wirelessLogic"));

        itemMaterial = new ItemMultiType(WirelessRedstone.creativeTab, "material").setUnlocalizedName("wrcbe:material");
        GameRegistry.register(itemMaterial);

        obsidianStick = itemMaterial.registerSubItem("obsidian_stick");
        stoneBowl = itemMaterial.registerSubItem("stone_bowl");
        retherPearl = itemMaterial.registerSubItem("rether_pearl");
        wirelessTransceiver = itemMaterial.registerSubItem("wireless_transceiver");
        blazeTransceiver = itemMaterial.registerSubItem("blaze_transceiver");
        receiverDish = itemMaterial.registerSubItem("receiver_dish");
        blazeReceiverDish = itemMaterial.registerSubItem("blaze_receiver_dish");

        OreDictionary.registerOre("obsidianRod", obsidianStick);
        OreDictionary.registerOre("stoneBowl", stoneBowl);

        triangulator = new ItemWirelessTriangulator();
        GameRegistry.register(triangulator.setRegistryName("triangulator"));
        remote = new ItemWirelessRemote();
        GameRegistry.register(remote.setRegistryName("remote"));
        sniffer = new ItemWirelessSniffer();
        GameRegistry.register(sniffer.setRegistryName("sniffer"));
        emptyWirelessMap = new ItemWirelessMap("empty_map");
        GameRegistry.register(emptyWirelessMap);
        wirelessMap = new ItemWirelessMap("map");
        GameRegistry.register(wirelessMap);
        tracker = new ItemWirelessTracker();
        GameRegistry.register(tracker.setRegistryName("tracker"));
        itemRep = new ItemREP();
        GameRegistry.register(itemRep.setRegistryName("rep"));
        pSniffer = new ItemPrivateSniffer();
        GameRegistry.register(pSniffer.setRegistryName("p_sniffer"));
    }


}
