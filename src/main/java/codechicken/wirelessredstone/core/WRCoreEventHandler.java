package codechicken.wirelessredstone.core;


import codechicken.lib.util.ServerUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WRCoreEventHandler
{
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote)
            RedstoneEther.loadClientEther(event.getWorld());
        else
            RedstoneEther.loadServerWorld(event.getWorld());
    }

    @SubscribeEvent
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        RedstoneEther.loadServerWorld(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote)
            return;

        RedstoneEther.unloadServerWorld(event.getWorld());

        if (!ServerUtils.mc().isServerRunning())
            RedstoneEther.unloadServer();
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld().isRemote || RedstoneEther.server() == null)
            return;

        RedstoneEther.server().saveEther(event.getWorld());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld().isRemote)
            return;

        if (RedstoneEther.server() != null)//new world
        {
            RedstoneEther.loadServerWorld(event.getWorld());
            RedstoneEther.server().verifyChunkTransmitters(event.getWorld(), event.getChunk().xPosition, event.getChunk().zPosition);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RenderWirelessBolt.render(event.getPartialTicks(), Minecraft.getMinecraft().getRenderViewEntity());
    }

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
    }

    @SubscribeEvent
    public void playerDimensionChange(PlayerChangedDimensionEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
    }

    @SubscribeEvent
    public void playerLogout(PlayerLoggedOutEvent event) {
        RedstoneEther.server().removePlayer(event.player);
    }

    @SubscribeEvent
    public void playerRespawn(PlayerRespawnEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if(event.phase == Phase.START)
            WirelessBolt.update(WirelessBolt.clientboltlist);
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent event) {
        if(event.phase == Phase.START)
            WirelessBolt.update(WirelessBolt.serverboltlist);
    }

    @SubscribeEvent
    public void serverTick(WorldTickEvent event) {
        if(event.phase == Phase.END && !event.world.isRemote)
            RedstoneEther.server().tick(event.world);
    }
}
