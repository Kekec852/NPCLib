package com.topcat.npclib.nms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_6_R3.AxisAlignedBB;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.PlayerChunkMap;
import net.minecraft.server.v1_6_R3.WorldProvider;
import net.minecraft.server.v1_6_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author martin
 */
public class BWorld {

    private BServer server;
    private final World world;
    private CraftWorld cWorld;
    private WorldServer wServer;
    private WorldProvider wProvider;

    public BWorld(BServer server, String worldName) {
        this.server = server;
        world = server.getServer().getWorld(worldName);
        try {
            cWorld = (CraftWorld) world;
            wServer = cWorld.getHandle();
            wProvider = wServer.worldProvider;
        } catch (final Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
    }

    public BWorld(World world) {
        this.world = world;
        try {
            cWorld = (CraftWorld) world;
            wServer = cWorld.getHandle();
            wProvider = wServer.worldProvider;
        } catch (final Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
    }

    public PlayerChunkMap getChunkMap() {
        return wServer.getPlayerChunkMap();
    }

    public CraftWorld getCraftWorld() {
        return cWorld;
    }

    public WorldServer getWorldServer() {
        return wServer;
    }

    public WorldProvider getWorldProvider() {
        return wProvider;
    }

    public boolean createExplosion(double x, double y, double z, float power) {
        return wServer.explode(null, x, y, z, power, false).wasCanceled ? false : true;
    }

    public boolean createExplosion(Location l, float power) {
        return wServer.explode(null, l.getX(), l.getY(), l.getZ(), power, false).wasCanceled ? false : true;
    }

    @SuppressWarnings ("unchecked")
    public void removeEntity(final Player player, JavaPlugin plugin) {
        server.getServer().getScheduler().callSyncMethod(plugin, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                final Location loc = player.getLocation();
                final CraftWorld craftWorld = (CraftWorld) player.getWorld();
                final CraftPlayer craftPlayer = (CraftPlayer) player;

                final double x = loc.getX() + 0.5;
                final double y = loc.getY() + 0.5;
                final double z = loc.getZ() + 0.5;
                final double radius = 10;

                List<Entity> entities = new ArrayList<>();
                final AxisAlignedBB bb = AxisAlignedBB.a(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
                entities = craftWorld.getHandle().getEntities(craftPlayer.getHandle(), bb);
                for (final Entity o : entities) {
                    if (!(o instanceof EntityPlayer)) {
                        o.getBukkitEntity().remove();
                    }
                }
                return null;
            }
        });
    }
}