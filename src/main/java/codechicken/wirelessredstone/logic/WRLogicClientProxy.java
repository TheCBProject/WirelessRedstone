package codechicken.wirelessredstone.logic;

import codechicken.core.ModDescriptionEnhancer;
import codechicken.lib.texture.TextureUtils;
import net.minecraftforge.client.MinecraftForgeClient;

import static codechicken.wirelessredstone.logic.WirelessRedstoneLogic.*;

public class WRLogicClientProxy extends WRLogicProxy
{
    @Override
    public void init() {
        super.init();
        TextureUtils.addIconRegister(new RenderWireless());
        ModDescriptionEnhancer.enhanceMod("WR-CBE|Logic");

        //MinecraftForgeClient.registerItemRenderer(itemwireless, new ItemWirelessRenderer());
    }
}
