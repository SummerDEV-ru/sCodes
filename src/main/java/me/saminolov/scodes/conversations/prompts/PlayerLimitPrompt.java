package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class PlayerLimitPrompt extends StringPrompt {

    private final Main plugin;

    public PlayerLimitPrompt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        String codeName = (String) context.getSessionData("code_name");
        return ChatColor.GRAY + "Код: " + ChatColor.YELLOW + codeName + "\n" +
               ChatColor.WHITE + "Лимит использований " + ChatColor.YELLOW + "на одного игрока" + ChatColor.WHITE + ":\n" +
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
            context.setSessionData("player_limit", limit);
            return new TotalLimitPrompt(plugin);
        } catch (NumberFormatException e) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Введите корректное целое число!");
            return this;
        }
    }
}
