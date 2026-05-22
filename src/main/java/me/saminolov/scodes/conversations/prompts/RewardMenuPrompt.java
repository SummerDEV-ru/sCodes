package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.ArrayList;
import java.util.List;

public class RewardMenuPrompt extends StringPrompt {

    private final Main plugin;

    public RewardMenuPrompt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        List<String> rewards = getRewards(context);
        StringBuilder sb = new StringBuilder();

        sb.append(ChatColor.GOLD).append("════════ Настройка наград ════════\n");

        if (!rewards.isEmpty()) {
            sb.append(ChatColor.GREEN).append("Текущие награды (").append(rewards.size()).append("):\n");
            for (String r : rewards) {
                sb.append(ChatColor.GRAY).append("  › ").append(ChatColor.WHITE).append(formatReward(r)).append("\n");
            }
            sb.append("\n");
        } else {
            sb.append(ChatColor.GRAY).append("Наград пока нет.\n\n");
        }

        sb.append(ChatColor.YELLOW).append("Добавьте награду:\n");

        if (plugin.getLuckPerms().isEnabled()) {
            sb.append(ChatColor.GREEN).append("  [1] ").append(ChatColor.WHITE).append("Группа LuckPerms").append(ChatColor.GRAY).append(" (донат-привилегия)\n");
        } else {
            sb.append(ChatColor.DARK_GRAY).append("  [1] Группа LuckPerms").append(ChatColor.DARK_GRAY).append(" (не подключен)\n");
        }

        if (plugin.getPlayerPoints().isEnabled()) {
            sb.append(ChatColor.GREEN).append("  [2] ").append(ChatColor.WHITE).append("PlayerPoints").append(ChatColor.GRAY).append(" (релики)\n");
        } else {
            sb.append(ChatColor.DARK_GRAY).append("  [2] PlayerPoints").append(ChatColor.DARK_GRAY).append(" (не подключен)\n");
        }

        sb.append(ChatColor.GREEN).append("  [3] ").append(ChatColor.WHITE).append("Команда консоли\n");
        sb.append(ChatColor.RED).append("  [готово] ").append(ChatColor.WHITE).append("Завершить и создать код");

        return sb.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        switch (input.trim().toLowerCase()) {
            case "1":
                if (!plugin.getLuckPerms().isEnabled()) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "LuckPerms не подключен к серверу!");
                    return this;
                }
                return new LPGroupPrompt(plugin, this);

            case "2":
                if (!plugin.getPlayerPoints().isEnabled()) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "PlayerPoints не подключен к серверу!");
                    return this;
                }
                return new PPAmountPrompt(plugin, this);

            case "3":
                return new CustomCmdPrompt(plugin, this);

            case "готово":
            case "done":
            case "finish":
                return new ConfirmPrompt(plugin);

            default:
                context.getForWhom().sendRawMessage(
                        ChatColor.RED + "Введите 1, 2, 3 или 'готово'!");
                return this;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRewards(ConversationContext context) {
        Object obj = context.getSessionData("rewards");
        if (obj instanceof List) return (List<String>) obj;
        List<String> list = new ArrayList<>();
        context.setSessionData("rewards", list);
        return list;
    }

    public static void addReward(ConversationContext context, String reward) {
        getRewards(context).add(reward);
    }

    private String formatReward(String reward) {
        if (reward.startsWith("lp:"))  return "LuckPerms группа: " + ChatColor.AQUA + reward.substring(3);
        if (reward.startsWith("pp:"))  return "PlayerPoints: " + ChatColor.AQUA + reward.substring(3) + " реликов";
        if (reward.startsWith("cmd:")) return "Команда: " + ChatColor.AQUA + reward.substring(4);
        return reward;
    }
}
