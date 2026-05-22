package me.saminolov.scodes.conversations;

import me.saminolov.scodes.Main;
import me.saminolov.scodes.conversations.prompts.CodeNamePrompt;
import me.saminolov.scodes.utils.MessageUtils;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public final class CodeCreator {

    private CodeCreator() {}

    public static void start(Main plugin, Player player) {
        new ConversationFactory(plugin)
                .withModality(true)
                .withFirstPrompt(new CodeNamePrompt(plugin))
                .withEscapeSequence("отмена")
                .withLocalEcho(false)
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit()) {
                        player.sendMessage(MessageUtils.color(
                                plugin.getConfig().getString("messages.cancelled", "&6Коды &7» &cСоздание промо-кода отменено.")));
                    }
                })
                .buildConversation(player)
                .begin();
    }
}
