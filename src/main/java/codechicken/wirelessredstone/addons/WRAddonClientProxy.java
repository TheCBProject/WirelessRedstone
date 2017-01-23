package codechicken.wirelessredstone.addons;

import codechicken.core.ModDescriptionEnhancer;
import codechicken.wirelessredstone.core.SaveManager;
import codechicken.wirelessredstone.core.WRCoreCPH;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;

import static codechicken.wirelessredstone.addons.WirelessRedstoneAddons.*;

public class WRAddonClientProxy extends WRAddonProxy
{
    @Override
    public void init() {
        super.init();
        ModDescriptionEnhancer.enhanceMod("WR-CBE|Addons");

        WRCoreCPH.delegates.add(new WRAddonCPH());

        GuiWirelessSniffer.loadColours(SaveManager.config().getTag("addon"));
        //MinecraftForgeClient.registerItemRenderer(tracker, new RenderTracker());
        //MinecraftForgeClient.registerItemRenderer(wirelessMap, new WirelessMapRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityREP.class, new IRenderFactory<EntityREP>() {
            @Override
            public Render<? super EntityREP> createRenderFor(RenderManager manager) {
                return new RenderSnowball<EntityREP>(manager,rep, Minecraft.getMinecraft().getRenderItem());
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityWirelessTracker.class, new IRenderFactory<EntityWirelessTracker>() {
            @Override
            public Render<? super EntityWirelessTracker> createRenderFor(RenderManager manager) {
                return new RenderTracker(manager);
            }
        });
    }

    @Override
    public void openSnifferGui(EntityPlayer player) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiWirelessSniffer());
    }

    public void openPSnifferGui(EntityPlayer player) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiPrivateSniffer());
    }
}
