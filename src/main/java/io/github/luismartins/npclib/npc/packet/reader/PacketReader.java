package io.github.luismartins.npclib.npc.packet.reader;

import io.github.luismartins.npclib.NPCLib;
import io.github.luismartins.npclib.event.NPCInteractEvent;
import io.github.luismartins.npclib.npc.NPC;
import io.github.luismartins.npclib.npc.action.event.EventAction;
import io.github.luismartins.npclib.util.ReflectionUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.CancelledPacketHandleException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class PacketReader extends ChannelDuplexHandler {
    private Player p;

    public PacketReader(final Player p) {
        this.p = p;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            Integer id = Integer.parseInt(ReflectionUtil.getFieldValue(packet, "a").toString());
            String action = ReflectionUtil.getFieldValue(packet, "action").toString();

            for (NPC npc : NPCLib.getPlugin().getNpcProvider().getNpcs()) {
                if (npc.getID().equals(id)) {
                    try {
                        if (action.equalsIgnoreCase("ATTACK")) {
                            Bukkit.getServer().getPluginManager()
                                    .callEvent(new NPCInteractEvent(p, npc, EventAction.LEFT_CLICK));
                        } else if (action.equalsIgnoreCase("INTERACT")) {
                            Bukkit.getServer().getPluginManager()
                                    .callEvent(new NPCInteractEvent(p, npc, EventAction.RIGHT_CLICK));
                        }
                    } catch (CancelledPacketHandleException ignored) {
                    }
                }
            }
        }
        super.channelRead(context, packet);
    }
}