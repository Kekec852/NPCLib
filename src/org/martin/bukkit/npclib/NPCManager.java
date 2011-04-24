/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.bukkit.npclib;

import java.util.HashMap;
import net.minecraft.server.ItemInWorldManager;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author martin
 */
public class NPCManager {

    private HashMap<String, NPCEntity> npcs = new HashMap<String, NPCEntity>();
    private BServer server;
    private JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
        server = BServer.getInstance(plugin);
    }

    public NPCEntity spawnNPC(String name, Location l) {
        BWorld world = new BWorld(l.getWorld());
        NPCEntity npcEntity = new NPCEntity(server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager(world.getWorldServer()));
        npcEntity.setPositionRotation(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch());
        world.getWorldServer().getChunkAt(l.getWorld().getChunkAt(l).getX(), l.getWorld().getChunkAt(l).getZ()).a(npcEntity);
        //world.getWorldServer().manager.addPlayer(npcEntity);
        //server.getEntityTracker().a(npcEntity);
        //server.getEntityTracker().trackPlayer(npcEntity);
        world.getWorldServer().addEntity(npcEntity); //the right way
        npcs.put(name, npcEntity);
        return npcEntity;
    }

    public void despawn(String id) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npcs.remove(id);
            try {
                npc.world.removeEntity(npc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moveNPC(String npcName, Location l) {
        NPCEntity npc = npcs.get(npcName);
        if (npc != null) {
            npc.move(l.getX(), l.getY(), l.getZ());
        }
    }
	
	
    public NPCEntity getNPC(String name){
        return npcs.get(name);
    }
}
