package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import me.saminolov.scodes.models.PromoCode;
import me.saminolov.scodes.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.List;

public class ConfirmPrompt extends StringPrompt {

    private final Main plugin;

    public ConfirmPrompt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        String name = (String) context.getSessionData("code_name");
        int playerLimit = (int) context.getSessionData("player_limit");
        int totalLimit = (int) context.getSessionData("total_limit");
        List<String> rewards = RewardMenuPrompt.getRewards(context);

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD).append("════════ Подтверждение ════════\n");
        sb.append(ChatColor.WHITE).append("Код:          ").append(ChatColor.YELLOW).append(name).append("\n");
        sb.append(ChatColor.WHITE).append("Лимит/игрок:  ").append(ChatColor.YELLOW).append(playerLimit == 0 ? "∞" : playerLimit).append("\n");
        sb.append(ChatColor.WHITE).append("Общий лимит:  ").append(ChatColor.YELLOW).append(totalLimit == 0 ? "∞" : totalLimit).append("\n");
        sb.append(ChatColor.WHITE).append("Наград:       ").append(ChatColor.YELLOW).append(rewards.size()).append("\n");

        if (!rewards.isEmpty()) {
            for (String r : rewards) {
                sb.append(ChatColor.GRAY).append("  › ").append(ChatColor.WHITE).append(r).append("\n");
            }
        }

        sb.append("\n").append(ChatColor.GREEN).append("Создать код? ").append(ChatColor.WHITE).append("(да / нет)");
        return sb.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String lower = input.trim().toLowerCase();

        if (lower.equals("нет") || lower.equals("no") || lower.equals("н") || lower.equals("n")) {
            context.getForWhom().sendRawMessage(ChatColor.GRAY + "Создание кода отменено.");
            return END_OF_CONVERSATION;
        }

        if (lower.equals("да") || lower.equals("yes") || lower.equals("д") || lower.equals("y")) {
            String name = (String) context.getSessionData("code_name");
            int playerLimit = (int) context.getSessionData("player_limit");
            int totalLimit = (int) context.getSessionData("total_limit");
            List<String> rewards = RewardMenuPrompt.getRewards(context);

            PromoCode code = new PromoCode(name, playerLimit, totalLimit, rewards);
            plugin.getCodeManager().createCode(code);

            String msg = plugin.getConfig().getString("messages.code-created", "&6Коды &7» &aКод &6{code} &aсоздан!")
                    .replace("{code}", name);
            context.getForWhom().sendRawMessage(MessageUtils.color(msg));
            return END_OF_CONVERSATION;
        }

        context.getForWhom().sendRawMessage(ChatColor.RED + "Введите 'да' или 'нет'!");
        return this;
    }
}
