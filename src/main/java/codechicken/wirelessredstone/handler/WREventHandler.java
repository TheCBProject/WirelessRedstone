package codechicken.wirelessredstone.handler;

import codechicken.lib.util.ClientUtils;
import codechicken.lib.util.ServerUtils;
import codechicken.wirelessredstone.client.render.RenderWireless;
import codechicken.wirelessredstone.client.render.RenderWirelessBolt;
import codechicken.wirelessredstone.client.texture.TriangTexManager;
import codechicken.wirelessredstone.entity.EntityWirelessTracker;
import codechicken.wirelessredstone.entity.WirelessBolt;
import codechicken.wirelessredstone.manager.RedstoneEther;
import codechicken.wirelessredstone.manager.RedstoneEtherAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
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

/**
 * Created by covers1624 on 24/01/2017.
 */
public class WREventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote) {
            RedstoneEther.loadClientEther(event.getWorld());
            RedstoneEtherAddons.loadClientManager();
        } else {
            RedstoneEther.loadServerWorld(event.getWorld());
            RedstoneEtherAddons.loadServerWorld();
        }
    }

    @SubscribeEvent
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        RedstoneEther.loadServerWorld(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            return;
        }

        RedstoneEther.unloadServerWorld(event.getWorld());

        if (!ServerUtils.mc().isServerRunning()) {
            RedstoneEther.unloadServer();
            RedstoneEtherAddons.unloadServer();
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld().isRemote || RedstoneEther.server() == null) {
            return;
        }

        RedstoneEther.server().saveEther(event.getWorld());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld().isRemote) {
            return;
        }

        if (RedstoneEther.server() != null)//new world
        {
            RedstoneEther.loadServerWorld(event.getWorld());
            RedstoneEther.server().verifyChunkTransmitters(event.getWorld(), event.getChunk().x, event.getChunk().z);
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        Chunk chunk = event.getChunk();
        for (int i = 0; i < chunk.getEntityLists().length; ++i) {
            for (Object o : chunk.getEntityLists()[i]) {
                if (o instanceof EntityWirelessTracker) {
                    ((EntityWirelessTracker) o).onChunkUnload();
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly (Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RenderWirelessBolt.render(event.getPartialTicks(), Minecraft.getMinecraft().getRenderViewEntity());
    }

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
        RedstoneEtherAddons.server().onLogin(event.player);
    }

    @SubscribeEvent
    public void playerDimensionChange(PlayerChangedDimensionEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
        RedstoneEtherAddons.server().onDimensionChange(event.player);
    }

    @SubscribeEvent
    public void playerLogout(PlayerLoggedOutEvent event) {
        RedstoneEther.server().removePlayer(event.player);
        RedstoneEtherAddons.server().onLogout(event.player);
    }

    @SubscribeEvent
    public void playerRespawn(PlayerRespawnEvent event) {
        RedstoneEther.server().resetPlayer(event.player);
        RedstoneEtherAddons.server().onLogin(event.player);
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            WirelessBolt.update(WirelessBolt.clientboltlist);
        }

        if (ClientUtils.inWorld()) {
            if (event.phase == Phase.START) {
                TriangTexManager.processAllTextures();
            } else {
                RedstoneEtherAddons.client().tick();
            }
        }
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            WirelessBolt.update(WirelessBolt.serverboltlist);
            RedstoneEtherAddons.server().processTrackers();
        } else {
            RedstoneEtherAddons.server().tickTriangs();
            RedstoneEtherAddons.server().updateREPTimeouts();
        }
    }

    @SubscribeEvent
    public void serverTick(WorldTickEvent event) {
        if (!event.world.isRemote) {
            if (event.phase == Phase.END) {
                RedstoneEther.server().tick(event.world);
            } else {
                RedstoneEtherAddons.server().processSMPMaps(event.world);
            }
        }
    }

    @SubscribeEvent
    @SideOnly (Side.CLIENT)
    public void onTextureLoad(TextureStitchEvent.Pre event) {
        //RemoteTexManager.load(event.getMap());
        //TriangTexManager.loadTextures();
    }

    @SubscribeEvent
    @SideOnly (Side.CLIENT)
    public void onTextureLoadPost(TextureStitchEvent.Post event) {
        RenderWireless.postRegisterIcons();
    }
}
