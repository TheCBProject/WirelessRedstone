package codechicken.wirelessredstone.proxy;

import codechicken.core.CCUpdateChecker;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.IItemStackKeyGenerator;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.item.map.MapRenderRegistry;
import codechicken.lib.texture.TextureUtils;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.client.gui.GuiPrivateSniffer;
import codechicken.wirelessredstone.client.gui.GuiRedstoneWireless;
import codechicken.wirelessredstone.client.gui.GuiWirelessSniffer;
import codechicken.wirelessredstone.client.render.RenderTracker;
import codechicken.wirelessredstone.client.render.RenderWireless;
import codechicken.wirelessredstone.client.render.WirelessMapRenderer;
import codechicken.wirelessredstone.client.render.item.RenderItemTracker;
import codechicken.wirelessredstone.client.render.item.RenderItemWireless;
import codechicken.wirelessredstone.client.texture.RemoteTexManager;
import codechicken.wirelessredstone.client.texture.TriangTexManager;
import codechicken.wirelessredstone.entity.EntityREP;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.item.ItemWirelessRemote;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.SaveManager;
import codechicken.wirelessredstone.network.WRClientPH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import static codechicken.wirelessredstone.WirelessRedstone.NET_CHANNEL;
import static codechicken.wirelessredstone.init.ModItems.itemRep;

/**
 * Created by covers1624 on 23/01/2017.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {

        super.preInit();
        GuiWirelessSniffer.loadColours(SaveManager.config().getTag("addon"));
        TextureUtils.addIconRegister(new RenderWireless());
        TextureUtils.addIconRegister(new RemoteTexManager());
        TextureUtils.addIconRegister(new TriangTexManager());
        if (SaveManager.config().getTag("checkUpdates").getBooleanValue(true)) {
            CCUpdateChecker.updateCheck("WR-CBE", WirelessRedstone.class.getAnnotation(Mod.class).version());
        }
        PacketCustom.assignHandler(NET_CHANNEL, new WRClientPH());

        MapRenderRegistry.registerMapRenderer(ModItems.itemWirelessMap, new WirelessMapRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityREP.class, new IRenderFactory<EntityREP>() {
            @Override
            public Render<? super EntityREP> createRenderFor(RenderManager manager) {

                return new RenderSnowball<EntityREP>(manager, itemRep, Minecraft.getMinecraft().getRenderItem());
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityWirelessTracker.class, new IRenderFactory<EntityWirelessTracker>() {
            @Override
            public Render<? super EntityWirelessTracker> createRenderFor(RenderManager manager) {

                return new RenderTracker(manager);
            }
        });
        registerModels();
    }

    private void registerModels() {

        ModelRegistryHelper.registerItemRenderer(ModItems.itemWireless, new RenderItemWireless());

        ModItems.itemMaterial.registerModelVariants();
        ModelResourceLocation location = new ModelResourceLocation("wrcbe:material", "type=rep");
        ModelLoader.setCustomModelResourceLocation(ModItems.itemRep, 0, location);

        ModelLoader.setCustomMeshDefinition(ModItems.itemRemote, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("wrcbe:device", "type=wireless_remote");
            }
        });
        ModelLoader.registerItemVariants(ModItems.itemRemote, new ModelResourceLocation("wrcbe:device", "type=wireless_remote"));
        BlockBakery.registerItemKeyGenerator(ModItems.itemRemote, new IItemStackKeyGenerator() {
            @Override
            public String generateKey(ItemStack stack) {
                return stack.getItem().getRegistryName().toString() + "|" + stack.getMetadata() + "," + ItemWirelessRemote.getTransmitting(stack);
            }
        });

        ModelLoader.setCustomMeshDefinition(ModItems.itemTriangulator, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("wrcbe:device", "type=wireless_triangulator");
            }
        });
        ModelLoader.registerItemVariants(ModItems.itemTriangulator, new ModelResourceLocation("wrcbe:device", "type=wireless_triangulator"));
        BlockBakery.registerItemKeyGenerator(ModItems.itemTriangulator, new IItemStackKeyGenerator() {
            @Override
            public String generateKey(ItemStack stack) {
                int damage = stack.getItemDamage();
                if (damage < 0 || damage > RedstoneEther.numfreqs) {
                    damage = 0;
                }
                //I know, random call to a thing, used to update textures.
                TriangTexManager.getIconIndexFromDamage(damage);
                return BlockBakery.defaultItemKeyGenerator.generateKey(stack);
            }
        });
        ModelLoader.setCustomModelResourceLocation(ModItems.itemSniffer, 0, new ModelResourceLocation("wrcbe:device", "type=sniffer"));
        ModelLoader.setCustomModelResourceLocation(ModItems.itemPrivateSniffer, 0, new ModelResourceLocation("wrcbe:device", "type=private_sniffer"));

        ModelRegistryHelper.registerItemRenderer(ModItems.itemTracker, new RenderItemTracker());
        ModelLoader.setCustomModelResourceLocation(ModItems.itemEmptyWirelessMap, 0, new ModelResourceLocation("wrcbe:device", "type=empty_wireless_map"));
        ModelLoader.registerItemVariants(ModItems.itemWirelessMap, new ModelResourceLocation("wrcbe:device", "type=wireless_map"));
        ModelLoader.setCustomMeshDefinition(ModItems.itemWirelessMap, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("wrcbe:device", "type=wireless_map");
            }
        });
    }

    @Override
    public void init() {

        super.init();
    }

    @Override
    public void postInit() {

        super.postInit();
    }

    @Override
    public void openItemWirelessGui(EntityPlayer entityplayer) {

        Minecraft.getMinecraft().displayGuiScreen(new GuiRedstoneWireless(entityplayer.inventory));
    }

    @Override
    public void openTileWirelessGui(EntityPlayer entityplayer, ITileWireless tile) {

        Minecraft.getMinecraft().displayGuiScreen(new GuiRedstoneWireless(entityplayer.inventory, tile));
    }

    @Override
    public void openSnifferGui(EntityPlayer player) {

        Minecraft.getMinecraft().displayGuiScreen(new GuiWirelessSniffer());
    }

    @Override
    public void openPSnifferGui(EntityPlayer player) {

        Minecraft.getMinecraft().displayGuiScreen(new GuiPrivateSniffer());
    }

}
