package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.Collection;

public class LPGroupPrompt extends StringPrompt {

    private final Main plugin;
    private final RewardMenuPrompt parent;

    public LPGroupPrompt(Main plugin, RewardMenuPrompt parent) {
        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        Collection<String> groups = plugin.getLuckPerms().getGroups();
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD).append("══ Группа LuckPerms (донат) ══\n");

        if (!groups.isEmpty()) {
            sb.append(ChatColor.YELLOW).append("Доступные группы:\n");
            for (String g : groups) {
                sb.append(ChatColor.GRAY).append("  › ").append(ChatColor.WHITE).append(g).append("\n");
            }
        } else {
            sb.append(ChatColor.GRAY).append("(группы не загружены, введите название вручную)\n");
        }

        sb.append(ChatColor.WHITE).append("Введите название группы:\n");
        sb.append(ChatColor.GRAY).append("('назад' для возврата в меню наград)");
        return sb.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String group = input.trim().toLowerCase();

        if (group.equals("назад") || group.equals("back")) {
            return parent;
        }
        if (group.isEmpty()) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Название группы не может быть пустым!");
            return this;
        }

        Collection<String> groups = plugin.getLuckPerms().getGroups();
        if (!groups.isEmpty() && !groups.contains(group)) {
            context.getForWhom().sendRawMessage(
                    ChatColor.RED + "Группа '" + group + "' не найдена в LuckPerms!\n" +
                    ChatColor.GRAY + "Введите точное название из списка выше.");
            return this;
        }

        RewardMenuPrompt.addReward(context, "lp:" + group);
        context.getForWhom().sendRawMessage(
                ChatColor.GREEN + "✔ Добавлена группа LuckPerms: " + ChatColor.YELLOW + group);
        return parent;
    }
}
