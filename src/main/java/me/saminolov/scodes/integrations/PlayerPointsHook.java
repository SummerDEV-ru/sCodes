package me.saminolov.scodes.integrations;

import me.saminolov.scodes.Main;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerPointsHook {

    private PlayerPointsAPI api;
    private final boolean enabled;

    public PlayerPointsHook(Main plugin) {
        Plugin pp = plugin.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (pp instanceof PlayerPoints) {
            this.api = ((PlayerPoints) pp).getAPI();
            this.enabled = true;
            plugin.getLogger().info("PlayerPoints успешно подключен.");
        } else {
            this.enabled = false;
            plugin.getLogger().warning("PlayerPoints не найден. Релики будут недоступны.");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void give(Player player, int amount) {
        if (!enabled) return;
        api.give(player.getUniqueId(), amount);
    }
}
