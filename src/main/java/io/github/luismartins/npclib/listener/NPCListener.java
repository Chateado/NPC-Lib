package io.github.luismartins.npclib.listener;

import io.github.luismartins.npclib.NPCLib;
import io.github.luismartins.npclib.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.Objects;

public class NPCListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        ArrayList<NPC> n = new ArrayList<NPC>();
        for (NPC npc : NPCLib.getPlugin().getNpcProvider().getNpcs()) {
            if (npc.getLocation() == null || !isSameChunk(npc.getLocation(), chunk))
                continue;
            Player p = npc.getReceiver();
            if (npc.isHided())
                continue;
            if (p != null) {
                n.add(npc);
            }
        }
        for (NPC wantedNpcs : n) {
            wantedNpcs.despawn();
        }
        n = null;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        ArrayList<NPC> n = new ArrayList<NPC>();
        for (NPC npc : NPCLib.getPlugin().getNpcProvider().getNpcs()) {
            if (npc.getLocation() == null || !isSameChunk(npc.getLocation(), chunk))
                if (!npc.isHided())
                    return;
            Player player = npc.getReceiver();
            if (player == null)
                return;

            if (!Objects.equals(npc.getLocation().getWorld(), player.getWorld())) {
                continue;
            }
            double distanceSquared = player.getLocation().distanceSquared(npc.getLocation());
            boolean inRange = distanceSquared <= (70 * 70) || distanceSquared <= (Bukkit.getViewDistance() << 4);
            if (inRange) {
                n.add(npc);
            }
        }
        for (NPC wantedNpcs : n) {
            wantedNpcs.despawn();
        }
        n = null;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ())
            handleMove(event.getPlayer());
    }

    private static int getChunkCoordinate(int coordinate) {
        return coordinate >> 4;
    }

    private static boolean isSameChunk(Location loc, Chunk chunk) {
        return getChunkCoordinate(loc.getBlockX()) == chunk.getX()
                && getChunkCoordinate(loc.getBlockZ()) == chunk.getZ();
    }

    public static void handleMove(Player player) {
        ArrayList<NPC> n = new ArrayList<NPC>();
        for (NPC npc : NPCLib.getPlugin().getNpcProvider().getNpcs()) {
            if (npc.getReceiver() == player) {
                n.add(npc);
            }
        }
        for (NPC npcs : n) {
            if (npcs.isHided() && npcs.inRangeOf(player) && npcs.inViewOf(player)) {
                npcs.respawn();
            } else if (!npcs.isHided() && !npcs.inRangeOf(player)) {
                npcs.despawn();
            }
        }
        n = null;
    }
}
