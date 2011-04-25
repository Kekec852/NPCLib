package org.martin.bukkit.npclib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

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

    public NPCManager(JavaPlugin plugin) {
        server = BServer.getInstance(plugin);
    }

    public NPCEntity spawnNPC(String name, Location l) {
    	int i = 0;
    	String id = name;
    	while (npcs.containsKey(id)) {
    		id = name + i;
    		i++;
    	}
    	return spawnNPC(name, l, id);
    }
    
    public NPCEntity spawnNPC(String name, Location l, String id) {
    	if (npcs.containsKey(id)) {
    		server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
    		return npcs.get(id);
    	} else {
	    	if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
	    		String tmp = name.substring(0, 16);
	    		server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
	    		server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
	    		name = tmp;
	    	}
			BWorld world = new BWorld(l.getWorld());
	        NPCEntity npcEntity = new NPCEntity(server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager(world.getWorldServer()));
	        npcEntity.setPositionRotation(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch());
	        world.getWorldServer().getChunkAt(l.getWorld().getChunkAt(l).getX(), l.getWorld().getChunkAt(l).getZ()).a(npcEntity);
	        world.getWorldServer().addEntity(npcEntity); //the right way
	        npcs.put(id, npcEntity);
	        return npcEntity;
    	}
    }

    public void despawnById(String id) {
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
    
    public void despawn(String npcName) {
    	if (npcName.length() > 16) {
    		npcName = npcName.substring(0, 16); //Ensure you can still despawn
    	}
    	HashSet<String> toRemove = new HashSet<String>();
    	for(String n : npcs.keySet()) {
    		NPCEntity npc = npcs.get(n);
    		System.out.println(npc.name);
            if (npc != null && npc.name.equals(npcName)) {
                toRemove.add(n);
                try {
                    npc.world.removeEntity(npc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    	}
    	for (String n : toRemove) { npcs.remove(n); }
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