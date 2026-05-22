package me.saminolov.scodes.managers;

import me.saminolov.scodes.Main;
import me.saminolov.scodes.models.PromoCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CodeManager {

    private final Main plugin;
    private final Map<String, PromoCode> codes = new HashMap<>();

    private File dataFile;
    private FileConfiguration dataConfig;

    public CodeManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        loadDataFile();
        loadCodes();
    }

    private void loadDataFile() {
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadCodes() {
        codes.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("codes");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection cs = section.getConfigurationSection(key);
            if (cs == null) continue;

            int playerLimit = cs.getInt("player-limit", 1);
            int totalLimit = cs.getInt("total-limit", 0);
            int usedTotal = cs.getInt("used-total", 0);
            List<String> rewards = cs.getStringList("rewards");
            String msgSuccess = cs.getString("message-success", "");
            String msgPlayerLimit = cs.getString("message-player-limit", "");
            String msgTotalLimit = cs.getString("message-total-limit", "");

            PromoCode code = new PromoCode(key, playerLimit, totalLimit, rewards);
            code.setUsedTotal(usedTotal);
            code.setMessageSuccess(msgSuccess);
            code.setMessagePlayerLimit(msgPlayerLimit);
            code.setMessageTotalLimit(msgTotalLimit);
            codes.put(key.toLowerCase(), code);
        }
    }

    public void createCode(PromoCode code) {
        codes.put(code.getName().toLowerCase(), code);
        writeCodeToConfig(code);
        plugin.saveConfig();
    }

    private void writeCodeToConfig(PromoCode code) {
        String path = "codes." + code.getName();
        FileConfiguration config = plugin.getConfig();
        config.set(path + ".player-limit", code.getPlayerLimit());
        config.set(path + ".total-limit", code.getTotalLimit());
        config.set(path + ".used-total", code.getUsedTotal());
        config.set(path + ".rewards", code.getRewards());
        config.set(path + ".message-success", code.getMessageSuccess());
        config.set(path + ".message-player-limit", code.getMessagePlayerLimit());
        config.set(path + ".message-total-limit", code.getMessageTotalLimit());
    }

    public boolean deleteCode(String name) {
        String lower = name.toLowerCase();
        if (!codes.containsKey(lower)) return false;
        PromoCode code = codes.remove(lower);
        plugin.getConfig().set("codes." + code.getName(), null);
        plugin.saveConfig();
        return true;
    }

    public PromoCode getCode(String name) {
        return codes.get(name.toLowerCase());
    }

    public Map<String, PromoCode> getCodes() {
        return Collections.unmodifiableMap(codes);
    }

    public int getPlayerUses(String codeName, UUID playerId) {
        return dataConfig.getInt("usage." + playerId + "." + codeName.toLowerCase(), 0);
    }

    private void incrementPlayerUses(String codeName, UUID playerId) {
        String path = "usage." + playerId + "." + codeName.toLowerCase();
        dataConfig.set(path, dataConfig.getInt(path, 0) + 1);
        saveDataFile();
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения data.yml: " + e.getMessage());
        }
    }

    public ActivationResult activateCode(Player player, String codeName) {
        PromoCode code = getCode(codeName);
        if (code == null) return ActivationResult.NOT_FOUND;

        if (code.getTotalLimit() > 0 && code.getUsedTotal() >= code.getTotalLimit()) {
            return ActivationResult.TOTAL_LIMIT;
        }

        if (!player.hasPermission("scodes.bypass")) {
            int uses = getPlayerUses(codeName, player.getUniqueId());
            if (code.getPlayerLimit() > 0 && uses >= code.getPlayerLimit()) {
                return ActivationResult.PLAYER_LIMIT;
            }
        }

        for (String reward : code.getRewards()) {
            executeReward(player, reward);
        }

        code.incrementUsedTotal();
        incrementPlayerUses(codeName, player.getUniqueId());
        plugin.getConfig().set("codes." + code.getName() + ".used-total", code.getUsedTotal());
        plugin.saveConfig();

        return ActivationResult.SUCCESS;
    }

    private void executeReward(Player player, String reward) {
        try {
            if (reward.startsWith("lp:")) {
                String group = reward.substring(3).trim();
                if (!plugin.getLuckPerms().isEnabled()) {
                    plugin.getLogger().warning("LuckPerms не подключен, пропуск награды: " + reward);
                    return;
                }
                plugin.getLuckPerms().addGroup(player, group);
                plugin.getLogger().info("Выдана группа LuckPerms '" + group + "' → " + player.getName());
            } else if (reward.startsWith("pp:")) {
                int amount = Integer.parseInt(reward.substring(3).trim());
                if (!plugin.getPlayerPoints().isEnabled()) {
                    plugin.getLogger().warning("PlayerPoints не подключен, пропуск награды: " + reward);
                    return;
                }
                plugin.getPlayerPoints().give(player, amount);
                plugin.getLogger().info("Выдано " + amount + " PlayerPoints → " + player.getName());
            } else if (reward.startsWith("cmd:")) {
                String cmd = reward.substring(4).replace("%player%", player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
                plugin.getLogger().info("Выполнена команда: " + cmd);
            } else {
                plugin.getLogger().warning("Неизвестный формат награды: " + reward);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при выдаче награды '" + reward + "' игроку " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public enum ActivationResult {
        SUCCESS, NOT_FOUND, PLAYER_LIMIT, TOTAL_LIMIT
    }
}
