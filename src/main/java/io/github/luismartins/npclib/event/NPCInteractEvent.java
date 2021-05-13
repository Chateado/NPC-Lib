package io.github.luismartins.npclib.event;

import io.github.luismartins.npclib.npc.NPC;
import io.github.luismartins.npclib.npc.action.event.EventAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private NPC npc;
    private EventAction action;

    public NPCInteractEvent(Player player, NPC npc, EventAction action) {
        this.player = player;
        this.npc = npc;
        this.action = action;
    }

    public Player getPlayer() {
        return player;
    }

    public NPC getNpc() {
        return npc;
    }

    public EventAction getAction() {
        return action;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}