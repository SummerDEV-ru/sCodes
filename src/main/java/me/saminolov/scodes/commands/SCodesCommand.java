package me.saminolov.scodes.commands;

import me.saminolov.scodes.Main;
import me.saminolov.scodes.conversations.CodeCreator;
import me.saminolov.scodes.managers.CodeManager;
import me.saminolov.scodes.models.PromoCode;
import me.saminolov.scodes.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SCodesCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public SCodesCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.color(
                    plugin.getConfig().getString("messages.only-player", "&6Коды &7» &cТолько игрок может использовать эту команду.")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player, label);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create":
                if (!player.hasPermission("scodes.admin")) {
                    MessageUtils.send(player, "no-permission");
                    return true;
                }
                CodeCreator.start(plugin, player);
                break;

            case "delete":
            case "del":
                if (!player.hasPermission("scodes.admin")) {
                    MessageUtils.send(player, "no-permission");
                    return true;
                }
                if (args.length < 2) {
                    MessageUtils.send(player, "usage");
                    return true;
                }
                if (plugin.getCodeManager().deleteCode(args[1])) {
                    MessageUtils.sendPlaceholder(player, "code-deleted", "code", args[1]);
                } else {
                    MessageUtils.send(player, "not-found");
                }
                break;

            case "list":
                if (!player.hasPermission("scodes.admin")) {
                    MessageUtils.send(player, "no-permission");
                    return true;
                }
                listCodes(player);
                break;

            case "info":
                if (!player.hasPermission("scodes.admin")) {
                    MessageUtils.send(player, "no-permission");
                    return true;
                }
                if (args.length < 2) {
                    MessageUtils.send(player, "usage");
                    return true;
                }
                showCodeInfo(player, args[1]);
                break;

            case "reload":
                if (!player.hasPermission("scodes.admin")) {
                    MessageUtils.send(player, "no-permission");
                    return true;
                }
                plugin.reloadConfig();
                plugin.getCodeManager().loadCodes();
                MessageUtils.send(player, "reload");
                break;

            default:
                handleActivation(player, sub);
                break;
        }

        return true;
    }

    private void handleActivation(Player player, String codeName) {
        CodeManager.ActivationResult result = plugin.getCodeManager().activateCode(player, codeName);
        PromoCode code = plugin.getCodeManager().getCode(codeName);

        switch (result) {
            case SUCCESS: {
                String msg = (code != null && !code.getMessageSuccess().isEmpty())
                        ? code.getMessageSuccess()
                        : plugin.getConfig().getString("messages.code-success", "&6Коды &7» &aКод активирован!");
                player.sendMessage(MessageUtils.color(msg));
                break;
            }
            case NOT_FOUND:
                MessageUtils.send(player, "not-found");
                break;
            case PLAYER_LIMIT: {
                String msg = (code != null && !code.getMessagePlayerLimit().isEmpty())
                        ? code.getMessagePlayerLimit()
                        : plugin.getConfig().getString("messages.player-limit-reached", "");
                if (!msg.isEmpty()) player.sendMessage(MessageUtils.color(msg));
                break;
            }
            case TOTAL_LIMIT: {
                String msg = (code != null && !code.getMessageTotalLimit().isEmpty())
                        ? code.getMessageTotalLimit()
                        : plugin.getConfig().getString("messages.limit-reached", "");
                if (!msg.isEmpty()) player.sendMessage(MessageUtils.color(msg));
                break;
            }
        }
    }

    private void listCodes(Player player) {
        Map<String, PromoCode> codes = plugin.getCodeManager().getCodes();
        if (codes.isEmpty()) {
            MessageUtils.send(player, "no-codes");
            return;
        }
        player.sendMessage(MessageUtils.color("&6Коды &7» &fСписок промо-кодов &7(&e" + codes.size() + "&7)"));
        for (PromoCode code : codes.values()) {
            String uses = code.getUsedTotal() + (code.getTotalLimit() > 0 ? "&7/&f" + code.getTotalLimit() : "");
            player.sendMessage(MessageUtils.color(
                    "&7 › &6" + code.getName() +
                    " &7| Исп: &f" + uses +
                    " &7| Лимит: &f" + (code.getPlayerLimit() == 0 ? "∞" : code.getPlayerLimit()) +
                    " &7| Наград: &f" + code.getRewards().size()
            ));
        }
    }

    private void showCodeInfo(Player player, String codeName) {
        PromoCode code = plugin.getCodeManager().getCode(codeName);
        if (code == null) {
            MessageUtils.send(player, "not-found");
            return;
        }
        player.sendMessage(MessageUtils.color("&6Коды &7» &fИнформация о коде &6" + code.getName()));
        player.sendMessage(MessageUtils.color("&7 › Использовано: &f" + code.getUsedTotal() +
                (code.getTotalLimit() > 0 ? " &7/ &f" + code.getTotalLimit() : " &7(без лимита)")));
        player.sendMessage(MessageUtils.color("&7 › Лимит/игрок: &f" +
                (code.getPlayerLimit() == 0 ? "∞" : code.getPlayerLimit())));
        player.sendMessage(MessageUtils.color("&7 › Наград: &f" + code.getRewards().size()));
        for (String r : code.getRewards()) {
            player.sendMessage(MessageUtils.color("&7   - &f" + r));
        }
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " &6[код] &f- Ввести промокод"));
        if (player.hasPermission("scodes.admin")) {
            player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " create &7- Создать код"));
            player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " delete &6[код] &7- Удалить код"));
            player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " list &7- Список кодов"));
            player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " info &6[код] &7- Информация"));
            player.sendMessage(MessageUtils.color("&6Коды &7» &f/" + label + " reload &7- Перезагрузить конфиг"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        if (args.length == 1) {
            List<String> opts = new ArrayList<>(Arrays.asList("create", "delete", "list", "info", "reload"));
            opts.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
            return opts;
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("delete") || sub.equals("info")) {
                List<String> names = new ArrayList<>(plugin.getCodeManager().getCodes().keySet());
                names.removeIf(s -> !s.startsWith(args[1].toLowerCase()));
                return names;
            }
        }

        return Collections.emptyList();
    }
}
