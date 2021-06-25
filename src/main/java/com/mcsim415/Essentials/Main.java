package com.mcsim415.Essentials;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    int bf = 0;
    int hf = 1;
    String tag = "[월드서버] ";

    HashMap<UUID, Location> backs = new HashMap<>();
    HashMap<UUID, Location> homes = new HashMap<>();

    public void mapToFile(int type, @NotNull HashMap<UUID, Location> map) {
        for (UUID uuid : map.keySet()) {
            getConfig().set(type +"_"+uuid.toString(), map.get(uuid));
        }
        saveConfig();
    }

    @Override
    public void onEnable() {
        getLogger().info("플러그인이 성공적으로 로드 되었습니다!");
        getServer().getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) {
            try {
                getDataFolder().mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        saveConfig();
        File dfile = new File(getDataFolder(), "data.yml");
        if (dfile.length() == 0) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        getConfig();
    }

    @Override
    public void onDisable() {
        mapToFile(hf, homes);
        mapToFile(bf, backs);
        saveConfig();
        getLogger().info("플러그인이 성공적으로 언로드 되었습니다!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Location location = Objects.requireNonNull(player).getLocation();
        backs.put(player.getUniqueId(), location);
        mapToFile(bf, backs);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        Location location = player.getLocation();
        UUID uuid = player.getUniqueId();

        if (label.equalsIgnoreCase("sethome")) {
            homes.put(player.getUniqueId(), location);
            mapToFile(hf, homes);
            player.sendMessage(ChatColor.AQUA + tag + " 집이 성공적으로 설정되었습니다!");
        } else if (label.equalsIgnoreCase("home")) {
            if (getConfig().contains(hf +"_"+ uuid)) {
                Location hlocation = getConfig().getLocation(hf +"_"+ uuid);
                backs.put(player.getUniqueId(), location);
                mapToFile(bf, backs);

                player.teleport(Objects.requireNonNull(hlocation));
                player.sendMessage(ChatColor.AQUA + tag + " 집으로 이동되었습니다!");
            } else {
                player.sendMessage(ChatColor.RED + tag + " 먼저 집을 설정해주세요!");
            }
        } else if (label.equalsIgnoreCase("back")) {
            if (getConfig().contains(bf +"_"+ uuid)) {
                Location blocation = getConfig().getLocation(bf +"_"+ uuid);
                backs.put(uuid, location);
                mapToFile(bf, backs);

                player.teleport(Objects.requireNonNull(blocation));
                player.sendMessage(ChatColor.AQUA + tag + " 워프 전으로 이동되었습니다!");
            } else {
                player.sendMessage(ChatColor.RED + tag + " 이동한 기록이 없습니다!");
            }
        }
        return true;
    }

}