package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class PPAmountPrompt extends StringPrompt {

    private final Main plugin;
    private final RewardMenuPrompt parent;

    public PPAmountPrompt(Main plugin, RewardMenuPrompt parent) {
        this.plugin = plugin;
        this.parent = parent;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.GOLD + "══ PlayerPoints (Релики) ══\n" +
               ChatColor.WHITE + "Введите количество " + ChatColor.YELLOW + "реликов" + ChatColor.WHITE + " для выдачи:\n" +
               ChatColor.GRAY + "('назад' для возврата в меню наград)";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String trimmed = input.trim();

        if (trimmed.equalsIgnoreCase("назад") || trimmed.equalsIgnoreCase("back")) {
            return parent;
        }

        try {
            int amount = Integer.parseInt(trimmed);
            if (amount <= 0) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Количество реликов должно быть больше 0!");
                return this;
            }
            RewardMenuPrompt.addReward(context, "pp:" + amount);
            context.getForWhom().sendRawMessage(
                    ChatColor.GREEN + "✔ Добавлено: " + ChatColor.YELLOW + amount + " реликов");
            return parent;
        } catch (NumberFormatException e) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Введите корректное целое число!");
            return this;
        }
    }
}
