package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class TotalLimitPrompt extends StringPrompt {

    private final Main plugin;

    public TotalLimitPrompt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        int playerLimit = (int) context.getSessionData("player_limit");
        return ChatColor.GRAY + "Лимит/игрок: " + ChatColor.YELLOW + (playerLimit == 0 ? "∞" : playerLimit) + "\n" +
               ChatColor.WHITE + "Общий лимит " + ChatColor.YELLOW + "активаций кода" + ChatColor.WHITE + ":\n" +
               ChatColor.GRAY + "(0 = без ограничений)";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        try {
            int limit = Integer.parseInt(input.trim());
            if (limit < 0) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Введите число 0 или больше!");
                return this;
            }
            context.setSessionData("total_limit", limit);
            return new RewardMenuPrompt(plugin);
        } catch (NumberFormatException e) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Введите корректное целое число!");
            return this;
        }
    }
}
