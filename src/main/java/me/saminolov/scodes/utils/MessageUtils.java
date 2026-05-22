package me.saminolov.scodes.utils;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public final class MessageUtils {

    private static Main plugin;

    private MessageUtils() {}

    public static void init(Main p) {
        plugin = p;
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void send(Player player, String key) {
        player.sendMessage(color(plugin.getConfig().getString("messages." + key, key)));
    }

    public static void sendPlaceholder(Player player, String key, String placeholder, String value) {
        String msg = plugin.getConfig().getString("messages." + key, key);
        msg = msg.replace("{" + placeholder + "}", value);
        player.sendMessage(color(msg));
    }

    public static void sendRaw(Player player, String message) {
        player.sendMessage(color(message));
    }

    public static void sendToConversable(Conversable target, String message) {
        target.sendRawMessage(color(message));
    }
}
