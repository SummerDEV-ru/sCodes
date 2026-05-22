package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CustomCmdPrompt extends StringPrompt {

    private final Main plugin;
    private final RewardMenuPrompt parent;

    public CustomCmdPrompt(Main plugin, RewardMenuPrompt parent) {
        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.GOLD + "══ Команда консоли ══\n" +
               ChatColor.WHITE + "Введите команду " + ChatColor.YELLOW + "(без /)" + ChatColor.WHITE + ":\n" +
               ChatColor.GRAY + "Используйте %player% для ника игрока\n" +
               ChatColor.GRAY + "Пример: eco give %player% 1000\n" +
               ChatColor.GRAY + "('назад' для возврата в меню наград)";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String cmd = input.trim();

        if (cmd.equalsIgnoreCase("назад") || cmd.equalsIgnoreCase("back")) {
            return parent;
        }
        if (cmd.isEmpty()) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Команда не может быть пустой!");
            return this;
        }

        // Strip leading slash if accidentally entered
        if (cmd.startsWith("/")) cmd = cmd.substring(1);

        RewardMenuPrompt.addReward(context, "cmd:" + cmd);
        context.getForWhom().sendRawMessage(
                ChatColor.GREEN + "✔ Добавлена команда: " + ChatColor.YELLOW + cmd);
        return parent;
    }
}
