package io.github.luismartins.npclib.listener.demonstration;

import io.github.luismartins.npclib.NPCLib;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCDemo implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NPCLib.getPlugin().getNpcProvider().createNPC("§8" + RandomStringUtils.randomAlphanumeric(8), "ewogICJ0aW1lc3RhbXAiIDogMTYxOTEzMjMyNTkzMSwKICAicHJvZmlsZUlkIiA6ICI5OWRlNjYzNGRiMmY0OGMzODY3NWUzZDM0NTUwMzc1YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdWlzTWFhcnRpbnMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhmYTY2ZDE1OTU3NDZhMTdhMWNkMzI4MzZmOWE2NTVmNDBmODhkZDNjZDdkOThmZDhhN2U2OTU2YTZiM2I3ZSIKICAgIH0KICB9Cn0=", "MkdgfzfxEpwNde5lNGXjK/3i29eQ0ckzWXhFLfOMY30TFHei7dJ8ljimsk66+QTALVMh5+CmJEY324TpBunzZysrfHHREMMDmeuOfqevhq5Yu5020Yoza6hR3rjmWI5bJM9or0TQjfQeZlfM0PSK1z/E5/vYJAlLC3uOhJXf5D28AQKu+RlE8n+zXGdDhDlIaXizV3wucreKDmjcyF90WVfiNuVh9uh+IRNDQPwP8xx61N265RbSenuP3rrYtOwB7aoIbXDwdzmMFhOcCJ4g4ZyKker302+Th5Q8Uhmjk/HyYgHphA3Lpm+McobZo37oCgUyc2UyGDDNY6ki1oWyXJgT03JEu8wxyDQk/ivJO4SgfHunH7uVjiWwf+NMCgssd8STjvdnEi+Re1ZooQkgo0FFAMdljb+X6bfIM/mTunjQ93oGHFBc/znstdAVu/YrFXNBRBkZGurcGRHZUZjMgrESUzCTMXjuHdH+j93sj+nJQwfDMvQVWAAsTqBO78FlL6tvxlMRymt+6bb8ivkovDnoytq8HlfRo349bDtP5ozo0cQjJ8gNkGa/QHI787KxPOgzh+gi1hm8y1hgoVWFtUYC8dQ3/VE8slVlR6/JSG85QgQKtTl2L5GgEkWQLv3c3AEUGydYEJ+1zvzj1GKHaIGwd90kOx8OC3S6xGyAcV0=", new Location(Bukkit.getWorld("world"), 0.5, 80, -0.5), player);
    }
}
