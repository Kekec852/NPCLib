package org.martin.bukkit.npclib;

import java.lang.reflect.Field;
import java.net.Socket;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager(Socket socket, String s, NetHandler nethandler) {
        super(socket, s, nethandler);
        try {
            Field f = NetworkManager.class.getDeclaredField("j");
            f.setAccessible(true);
            f.set(this, false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void a(NetHandler nethandler) {
    }

    @Override
    public void a(Packet packet) {
    }

    @Override
    public void a(String s, Object... aobject) {
    }

    @Override
    public void a() {
    }

    @Override
    public void c() {
    }

    @Override
    public int d() {
        return 0;
    }
}
