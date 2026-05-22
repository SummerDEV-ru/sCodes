package me.saminolov.scodes;

import me.saminolov.scodes.commands.SCodesCommand;
import me.saminolov.scodes.integrations.LuckPermsHook;
import me.saminolov.scodes.integrations.PlayerPointsHook;
import me.saminolov.scodes.managers.CodeManager;
import me.saminolov.scodes.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private CodeManager codeManager;
    private LuckPermsHook luckPerms;
    private PlayerPointsHook playerPoints;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        MessageUtils.init(this);

        this.luckPerms = new LuckPermsHook(this);
        this.playerPoints = new PlayerPointsHook(this);
        this.codeManager = new CodeManager(this);

        SCodesCommand handler = new SCodesCommand(this);
        getCommand("scode").setExecutor(handler);
        getCommand("scode").setTabCompleter(handler);

        getLogger().info("══════════════════════════════════");
        getLogger().info("  sCodes v" + getDescription().getVersion() + " включен!");
        getLogger().info("  Автор: Saminolov | t.me/SummerDEV");
        getLogger().info("  LuckPerms:    " + (luckPerms.isEnabled()    ? "подключен ✔" : "не найден ✘"));
        getLogger().info("  PlayerPoints: " + (playerPoints.isEnabled() ? "подключен ✔" : "не найден ✘"));
        getLogger().info("  Кодов загружено: " + codeManager.getCodes().size());
        getLogger().info("══════════════════════════════════");
    }

    @Override
    public void onDisable() {
        getLogger().info("sCodes отключен.");
    }

    public static Main getInstance() {
        return instance;
    }

    public CodeManager getCodeManager() {
        return codeManager;
    }

    public LuckPermsHook getLuckPerms() {
        return luckPerms;
    }

    public PlayerPointsHook getPlayerPoints() {
        return playerPoints;
    }
}
