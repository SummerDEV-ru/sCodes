package me.saminolov.scodes.conversations.prompts;

import me.saminolov.scodes.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CodeNamePrompt extends StringPrompt {

    private final Main plugin;

    public CodeNamePrompt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.GOLD + "════════════════════════\n" +
               ChatColor.YELLOW + "  Создание промо-кода\n" +
               ChatColor.GOLD + "════════════════════════\n" +
               ChatColor.WHITE + "Введите " + ChatColor.YELLOW + "название кода" + ChatColor.WHITE + ":\n" +
               ChatColor.GRAY + "(только латиница, без пробелов | 'отмена' для выхода)";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        String codeName = input.trim().toLowerCase();

        if (codeName.isEmpty()) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Название не может быть пустым!");
            return this;
        }
        if (codeName.contains(" ")) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Название не должно содержать пробелы!");
            return this;
        }
        if (!codeName.matches("[a-z0-9_\\-]+")) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Только латинские буквы, цифры, _ и - !");
            return this;
        }
        if (plugin.getCodeManager().getCode(codeName) != null) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Код '" + codeName + "' уже существует!");
            return this;
        }

        context.setSessionData("code_name", codeName);
        return new PlayerLimitPrompt(plugin);
    }
}
