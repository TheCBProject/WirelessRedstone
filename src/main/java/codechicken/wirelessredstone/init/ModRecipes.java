package codechicken.wirelessredstone.init;

import codechicken.lib.util.ItemUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static codechicken.wirelessredstone.init.ModItems.*;

/**
 * Created by covers1624 on 24/01/2017.
 */
public class ModRecipes {

    public static void init() {
        //@formatter:off
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemWireless, 1, 0),
                "t  ",
                "srr",
                "fff",
                't', ModItems.wirelessTransceiver,
                's', "obsidianRod",
                'f', new ItemStack(Blocks.STONE_SLAB, 1, 0),
                'r', Items.REDSTONE));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemWireless, 1, 1),
                "d  ",
                "srr",
                "fff",
                'd', ModItems.receiverDish,
                's', "obsidianRod",
                'f', new ItemStack(Blocks.STONE_SLAB, 1, 0),
                'r', Items.REDSTONE));

        GameRegistry.addRecipe(new ItemStack(ModItems.itemWireless, 1, 2),
                "p  ",
                "srr",
                "fff",
                'p', ModItems.blazeTransceiver,
                's', Items.BLAZE_ROD,
                'f', new ItemStack(Blocks.STONE_SLAB, 1, 0),
                'r', Items.REDSTONE);

        GameRegistry.addRecipe(ItemUtils.copyStack(obsidianStick, 2),
                "O",
                "O",
                'O', Blocks.OBSIDIAN);
        GameRegistry.addRecipe(new ShapedOreRecipe(wirelessTransceiver,
                "r",
                "o",
                'r', retherPearl,
                'o', "obsidianRod"));

        GameRegistry.addRecipe(stoneBowl,
                "S S",
                " S ",
                'S', Blocks.STONE);
        GameRegistry.addRecipe(retherPearl,
                "rgr",
                "gpg",
                "rgr",
                'r', Items.REDSTONE,
                'g', Items.GLOWSTONE_DUST,
                'p', Items.ENDER_PEARL);
        GameRegistry.addRecipe(retherPearl,
                "grg",
                "rpr",
                "grg",
                'r', Items.REDSTONE,
                'g', Items.GLOWSTONE_DUST,
                'p', Items.ENDER_PEARL);
        GameRegistry.addRecipe(blazeTransceiver,
                "r",
                "b",
                'r', retherPearl,
                'b', Items.BLAZE_ROD);
        GameRegistry.addRecipe(new ShapedOreRecipe(receiverDish,
                "t",
                "b",
                't', wirelessTransceiver,
                'b', "stoneBowl"));
        GameRegistry.addRecipe(new ShapedOreRecipe(blazeReceiverDish,
                "t",
                "b",
                't', blazeTransceiver,
                'b', "stoneBowl"));

        GameRegistry.addRecipe(new ItemStack(ModItems.triangulator), " i ",
                "iti",
                " i ",
                'i', Items.IRON_INGOT,
                't', ModItems.wirelessTransceiver);

        GameRegistry.addRecipe(new ItemStack(ModItems.itemRemote), "t",
                "B",
                'B', Blocks.STONE_BUTTON,
                't', ModItems.wirelessTransceiver);

        GameRegistry.addRecipe(new ItemStack(ModItems.sniffer), "dtd",
                "rBr",
                "SSS",
                'd', ModItems.receiverDish,
                't', ModItems.wirelessTransceiver,
                'r', Items.REDSTONE,
                'B', Blocks.STONE_BUTTON,
                'S', Blocks.STONE);

        GameRegistry.addRecipe(new ItemStack(ModItems.wirelessMap, 1), "ppp",
                "ptp",
                "ppp",
                'p', Items.PAPER,
                't', new ItemStack(ModItems.triangulator, 1, OreDictionary.WILDCARD_VALUE));

        GameRegistry.addRecipe(new ItemStack(ModItems.itemRep), " Ot",
                "OpO",
                "tO ",
                'p', ModItems.retherPearl,
                't', ModItems.blazeTransceiver,
                'r', Items.REDSTONE,
                'g', Items.GLOWSTONE_DUST,
                'O', Blocks.OBSIDIAN);

        GameRegistry.addRecipe(new ItemStack(ModItems.tracker, 1), " p ",
                "OOO",
                " s ",
                'p', ModItems.retherPearl,
                's', Items.SLIME_BALL,
                'O', Blocks.OBSIDIAN);

        GameRegistry.addRecipe(new ItemStack(ModItems.pSniffer), "dtd",
                "rBr",
                "SSS",
                'd', ModItems.blazeReceiverDish,
                't', ModItems.blazeTransceiver,
                'r', Items.REDSTONE,
                'B', Blocks.STONE_BUTTON,
                'S', Blocks.STONE);
        //@formatter:on
    }

}
