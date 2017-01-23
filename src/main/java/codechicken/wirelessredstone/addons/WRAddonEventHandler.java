package codechicken.wirelessredstone.addons;

import codechicken.lib.util.ClientUtils;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WRAddonEventHandler
{
    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {
        RedstoneEtherAddons.server().onLogin(event.player);
    }

    @SubscribeEvent
    public void playerLogout(PlayerLoggedOutEvent event) {
        RedstoneEtherAddons.server().onLogout(event.player);
    }

    @SubscribeEvent
    public void playerDimensionChange(PlayerChangedDimensionEvent event) {
        RedstoneEtherAddons.server().onDimensionChange(event.player);
    }

    @SubscribeEvent
    public void playerRespawn(PlayerRespawnEvent event) {
        RedstoneEtherAddons.server().onLogin(event.player);
    }

    @SubscribeEvent
    public void worldTick(WorldTickEvent event) {
        if(event.phase == Phase.START)
            RedstoneEtherAddons.server().processSMPMaps(event.world);
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent event) {
        if(event.phase == Phase.START)
            RedstoneEtherAddons.server().processTrackers();
        else {
            RedstoneEtherAddons.server().tickTriangs();
            RedstoneEtherAddons.server().updateREPTimeouts();
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if(ClientUtils.inWorld()) {
            if (event.phase == Phase.START)
                TriangTexManager.processAllTextures();
            else
                RedstoneEtherAddons.client().tick();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(Load event) {
        if (event.getWorld().isRemote)
            RedstoneEtherAddons.loadClientManager();
        else
            RedstoneEtherAddons.loadServerWorld();
    }

    @SubscribeEvent
    public void onChunkUnload(Unload event) {
        Chunk chunk = event.getChunk();
        for (int i = 0; i < chunk.getEntityLists().length; ++i) {
            for (Object o : chunk.getEntityLists()[i]) {
                if (o instanceof EntityWirelessTracker)
                    ((EntityWirelessTracker) o).onChunkUnload();
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureLoad(TextureStitchEvent.Pre event) {
        RemoteTexManager.load(event.getMap());
        TriangTexManager.loadTextures();
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (event.getWorld().isRemote)
            return;

        if (!ServerUtils.mc().isServerRunning())
            RedstoneEtherAddons.unloadServer();
    }
}
