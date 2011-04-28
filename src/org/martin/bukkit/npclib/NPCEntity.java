package org.martin.bukkit.npclib;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.HumanEntity;

/**
 *
 * @author martin
 */
public class NPCEntity extends EntityPlayer {

    private int lastTargetId;
    private long lastBounceTick;
    private int lastBounceId;
    private NPCPath path;
    private Block last;
    private Timer t = new Timer();
    private Location end;
    private int maxIter;
    
    public void pathFindTo(Location l, int maxIterations) {
        path = new NPCPath(getBukkitEntity().getLocation(), l, maxIterations);
        end = l;
        maxIter = maxIterations;
        t.schedule(new movePath(), 300);
    }
    
    public class movePath extends TimerTask {

        @Override
        public void run() {
            if (path != null) {
                Block b = path.getNextBlock();
                float angle = yaw;
                if (b != null) {
                    if (path.standon.contains(b.getType())) {
                        if (last != null) {
                            angle = ((float) Math.toDegrees(Math.atan2(last.getZ() - b.getZ(), last.getX() - b.getX()))) + 90;
                        }
                        setPositionRotation(b.getX() + 0.5, b.getY(), b.getZ() + 0.5, angle, pitch);
                        t.schedule(new movePath(), 300);
                    } else {
                        pathFindTo(end, maxIter);
                    }
                } else if (last != null) {
                    setPositionRotation(end.getX(), end.getY(), end.getZ(), end.getYaw(), end.getPitch());
                }
                last = b;
            }
        }
        
    }

    public NPCEntity(MinecraftServer minecraftserver, World world, String s, ItemInWorldManager iteminworldmanager) {
        super(minecraftserver, world, s, iteminworldmanager);
        NetworkManager netMgr = new NPCNetworkManager(new NullSocket(), "NPC Manager", new NetHandler() {

            @Override
            public boolean c() {
                return true;
            }
        });
        this.netServerHandler = new NPCNetHandler(minecraftserver, netMgr, this);
        this.lastTargetId = -1;
        this.lastBounceId = -1;
        this.lastBounceTick = 0;
    }

    public void animateArmSwing() {
        this.b.tracker.a(this, new Packet18ArmAnimation(this, 1));
    }

    public void actAsHurt() {
        this.b.tracker.a(this, new Packet18ArmAnimation(this, 2));
    }

    @Override
    public boolean a(EntityHuman entity) {

        EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
        CraftServer server = ((WorldServer) this.world).getServer();
        server.getPluginManager().callEvent(event);

        return super.a(entity);
    }

    @Override
    public void b(EntityHuman entity) {
        if (lastTargetId == -1 || lastTargetId != entity.id) {
            EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);
        }
        lastTargetId = entity.id;

        super.b(entity);
    }

    @Override
    public void c(Entity entity) {
        if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
            EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);

            lastBounceTick = System.currentTimeMillis();
        }

        lastBounceId = entity.id;

        super.c(entity);
    }

    @Override
    public void a(Entity entity) {
        System.out.println(entity);
        super.a(entity);
    }

    @Override
    public void a(EntityLiving entityliving) {
        System.out.println(entityliving);
        super.a(entityliving);
    }

    @Override
    public void setPositionRotation(double x, double y, double z, float yaw, float pitch) {
        super.setPositionRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void move(double x, double y, double z) {
        super.move(x, y, z);
    }

    public void setItemInHand(Material m) {
        ((HumanEntity) getBukkitEntity()).setItemInHand(new ItemStack(m, 1));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
