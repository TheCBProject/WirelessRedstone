package codechicken.wirelessredstone.proxy;

import codechicken.core.CCUpdateChecker;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.texture.TextureUtils;
import codechicken.wirelessredstone.WirelessRedstone;
import codechicken.wirelessredstone.client.gui.GuiPrivateSniffer;
import codechicken.wirelessredstone.client.render.item.RenderItemWireless;
import codechicken.wirelessredstone.entity.EntityREP;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.client.gui.GuiWirelessSniffer;
import codechicken.wirelessredstone.client.render.RenderTracker;
import codechicken.wirelessredstone.api.ITileWireless;
import codechicken.wirelessredstone.client.gui.GuiRedstoneWireless;
import codechicken.wirelessredstone.client.render.RenderWireless;
import codechicken.wirelessredstone.init.ModItems;
import codechicken.wirelessredstone.network.WRClientPH;
import codechicken.wirelessredstone.manager.SaveManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
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
        if (SaveManager.config().getTag("checkUpdates").getBooleanValue(true)) {
            CCUpdateChecker.updateCheck("WR-CBE", WirelessRedstone.class.getAnnotation(Mod.class).version());
        }
        PacketCustom.assignHandler(NET_CHANNEL, new WRClientPH());

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
