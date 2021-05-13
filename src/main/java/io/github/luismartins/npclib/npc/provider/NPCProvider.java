package io.github.luismartins.npclib.npc.provider;

import io.github.luismartins.npclib.NPCLib;
import io.github.luismartins.npclib.listener.NPCListener;
import io.github.luismartins.npclib.npc.NPC;
import io.github.luismartins.npclib.npc.action.Action;
import io.github.luismartins.npclib.npc.packet.injector.PacketInjector;
import io.github.luismartins.npclib.util.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("deprecation")
public class NPCProvider {

    public List<NPC> npcs = new ArrayList<>();
    public PacketInjector packetInjector;

    public NPCProvider() {
        ReflectionUtil.init();
        packetInjector = new PacketInjector();
        Bukkit.getPluginManager().registerEvents(new NPCListener(), NPCLib.getPlugin());
    }

    public NPC createNPC(String name, String value, String signature, Location location, Player receiver) {
        NPC npc = ReflectionUtil.newNPC(name, value, signature, location, receiver, false);
        npc.spawn();
        Bukkit.getScheduler().runTaskLater(NPCLib.getPlugin(), () -> {
            npc.setAction(Action.REMOVE_PLAYER);
        }, 30L);
        npcs.add(npc);
        return npc;
    }

    public void removeNPC(NPC npc) {
        npcs.remove(npc);
        npc.despawn();
    }
}
