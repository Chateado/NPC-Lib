package io.github.luismartins.npclib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.luismartins.npclib.listener.NPCListener;
import io.github.luismartins.npclib.listener.demonstration.NPCDemo;
import io.github.luismartins.npclib.npc.provider.NPCProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class NPCLib extends JavaPlugin {

    @Getter
    private static NPCLib plugin;

    private NPCProvider npcProvider;

    private ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void onEnable() {
        npcProvider = new NPCProvider();

        //------------------------------- Demonstration listener -------------------------------//
        Bukkit.getPluginManager().registerEvents(new NPCDemo(), this);

    }

    public void onDisable() {

    }
}
