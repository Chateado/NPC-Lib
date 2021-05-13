package io.github.luismartins.npclib.npc;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.luismartins.npclib.NPCLib;
import io.github.luismartins.npclib.npc.action.Action;
import io.github.luismartins.npclib.npc.animation.Animation;
import io.github.luismartins.npclib.npc.equipment.EquipmentSlot;
import io.github.luismartins.npclib.npc.status.Status;
import io.github.luismartins.npclib.util.ReflectionUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
public abstract class NPC {

    private String name;
    private String value, signature;
    private Location location;
    private GameProfile gameProfile;
    private Player receiver;
    private Action action;
    private Boolean name_visible;
    private Boolean moving;
    private boolean hided;

    protected double cosFOV = Math.cos(Math.toRadians(60));

    public NPC(String name, String value, String signature, Location location, Player receiver, Action action,
               Boolean name_visible) {
        this.name = name;
        this.value = value;
        this.signature = signature;
        this.location = location;
        this.receiver = receiver;
        this.action = action;
        this.name_visible = name_visible;
        this.moving = false;
    }

    public void cancelMovement() {
        if (getMoving()) {
            moving = false;
            getWitch().remove();
        }
    }

    @SuppressWarnings("deprecation")
    public void spawn() {
        PlayerConnection playerConnection = ((CraftPlayer) getReceiver()).getHandle().playerConnection;
        if (playerConnection.isDisconnected())
            return;
        gameProfile = new GameProfile(UUID.randomUUID(), getName());
        gameProfile.getProperties().put("textures", new Property("textures", getValue(), getSignature()));
        setupEntity();
        ReflectionUtil.sendPacket(receiver, getPlayerInfoPacket());
        ReflectionUtil.sendPacket(receiver, getSpawnPacket());
        ReflectionUtil.sendPacket(receiver, getHeadRotationPacket());
        ReflectionUtil.sendPacket(receiver, getEntityMetaPacket());
        if (!name_visible) {
            ReflectionUtil.sendPacket(receiver, getHideNamePacket());
        }
        NPCLib.getPlugin().getNpcProvider().getPacketInjector().addPlayer(receiver);
        setHided(false);

        int batEntityId = next();

        PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
        setFieldValue(packet, "a", 0);
        setFieldValue(packet, "b", batEntityId);
        setFieldValue(packet, "c", this.getEntity().getId());
        playerConnection.sendPacket(buildAttachPacket(batEntityId, this.getEntity().getId()));

    }

    private static PacketPlayOutAttachEntity buildAttachPacket(int a, int b) {
        PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity();
        setFieldValue(packet, "a", 0);
        setFieldValue(packet, "b", a);
        setFieldValue(packet, "c", b);
        return packet;
    }

    private static void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public void teleport(Location location) {
        setLocation(location);
        ReflectionUtil.sendPacket(receiver, getTeleportPacket(location));
        ReflectionUtil.sendPacket(receiver, getHeadRotationPacket());
    }

    public void respawn() {
        NPCLib.getPlugin().getNpcProvider().removeNPC(this);
        NPCLib.getPlugin().getNpcProvider().createNPC(getName(), getValue(), getSignature(), getLocation(), getReceiver());
    }

    public void setTarget(Location target, Float speed, Double distance_stop) {
        if (getLocation().distance(target) > 15.95) {
            System.out.println("[human] The distance between the locations can't be up to 15.0 blocks.");
            return;
        }
        if (getMoving()) {
            cancelMovement();
        }
        targetWitch(target, speed);
        moving = true;
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (getMoving()) {
                    if (getWitch().getLocation().distance(target) <= distance_stop) {
                        cancelMovement();
                        this.cancel();
                    } else {
                        setLocation(getWitch().getLocation());
                        ReflectionUtil.sendPacket(receiver, getHeadRotationPacket());
                        teleport(getWitch().getLocation());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(NPCLib.getPlugin(), 0, 1L);
    }

    public static synchronized int next() {
        try {
            Class<?> clazz = MinecraftReflection.getEntityClass();
            Field field = clazz.getDeclaredField("entityCount");
            field.setAccessible(true);
            int id = field.getInt(null);
            field.set(null, id + 1);
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    public void setTarget(Entity entity, Float speed, Double distance_stop) {
        if (getLocation().distance(entity.getLocation()) > 15.95) {
            System.out.println("[human] The distance between the locations can't be up to 15.0 blocks.");
            return;
        }
        if (getMoving()) {
            cancelMovement();
        }
        moving = true;
        targetWitch(entity.getLocation(), speed);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getMoving()) {
                    if (!entity.isValid()) {
                        cancel();
                        this.cancel();
                        return;
                    }
                    if (getLocation().distance(entity.getLocation()) > 15.95) {
                        System.out.println("[human] The distance between the locations can't be up to 15.0 blocks.");
                        cancelMovement();
                        this.cancel();
                        return;
                    }
                    if (getWitch().getLocation().distance(entity.getLocation()) <= distance_stop) {
                        Vector dirBetweenLocations = entity.getLocation().toVector().subtract(getLocation().toVector());
                        Location target = getWitch().getLocation().clone();
                        target.setDirection(dirBetweenLocations);
                        setLocation(target);
                        teleport(target);
                        removePathFinders(getWitch());
                    } else {
                        Vector dirBetweenLocations = entity.getLocation().toVector().subtract(getLocation().toVector());
                        Location target = getWitch().getLocation().clone();
                        target.setDirection(dirBetweenLocations);
                        setLocation(target);
                        teleport(target);
                        changeTarget(getWitch(), entity.getLocation(), speed);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(NPCLib.getPlugin(), 0, 1L);
    }

    @SuppressWarnings("deprecation")
    public void playAnimation(Animation animation) {
        ReflectionUtil.sendPacket(receiver, getAnimationPacket(animation));
    }

    @SuppressWarnings("deprecation")
    public void playStatus(Status status) {
        ReflectionUtil.sendPacket(receiver, getStatusPacket(status));
    }

    @SuppressWarnings("deprecation")
    public void despawn() {
        if (getAction() == Action.ADD_PLAYER) {
            setAction(Action.REMOVE_PLAYER);
        }
        ReflectionUtil.sendPacket(receiver, getDestroyPacket());
        NPCLib.getPlugin().getNpcProvider().getPacketInjector().removePlayer(receiver);
        setHided(true);
    }

    @SuppressWarnings("deprecation")
    public void setAction(Action action) {
        this.action = action;
        ReflectionUtil.sendPacket(receiver, getPlayerInfoPacket());
    }

    @SuppressWarnings("deprecation")
    public void setEquipment(EquipmentSlot action, ItemStack itemStack) {
        ReflectionUtil.sendPacket(receiver, getEquipPacket(itemStack, action));
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Deprecated
    public abstract void setupEntity();

    @Deprecated
    public abstract void targetWitch(Location location, Float speed);

    @Deprecated
    public abstract void changeTarget(Entity entity, Location location, Float speed);

    @Deprecated
    public abstract void removePathFinders(Entity entity);

    @Deprecated
    public abstract Witch getWitch();

    @Deprecated
    public abstract Object getPlayerInfoPacket();

    @Deprecated
    public abstract Object getSpawnPacket();

    @Deprecated
    public abstract Object getDestroyPacket();

    @Deprecated
    public abstract Object getHeadRotationPacket();

    public abstract EntityPlayer getEntity();

    @Deprecated
    public abstract Object getTeleportPacket(Location loc);

    @Deprecated
    public abstract Object getAnimationPacket(Animation animation);

    @Deprecated
    public abstract Object getStatusPacket(Status status);

    @Deprecated
    public abstract Object getHideNamePacket();

    @Deprecated
    public abstract Object getEntityMetaPacket();

    @Deprecated
    public abstract Object getEquipPacket(ItemStack itemStack, EquipmentSlot action);

    @Deprecated
    public abstract Integer getID();

    public Boolean isHided() {
        return hided;
    }

    public void setHided(Boolean hided) {
        this.hided = hided;
    }

    public boolean inRangeOf(Player player) {
        if (player == null)
            return false;
        if (!player.getWorld().equals(location.getWorld())) {
            return false;
        }
        double distanceSquared = player.getLocation().distanceSquared(location);
        double bukkitRange = Bukkit.getViewDistance() << 4;
        return distanceSquared <= square(70) && distanceSquared <= square(bukkitRange);
    }

    public boolean inViewOf(Player player) {
        Vector dir = location.toVector().subtract(player.getEyeLocation().toVector()).normalize();
        return dir.dot(player.getEyeLocation().getDirection()) >= cosFOV;
    }

    private double square(double val) {
        return val * val;
    }

}