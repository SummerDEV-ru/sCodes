package me.saminolov.scodes.integrations;

import me.saminolov.scodes.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LuckPermsHook {

    private LuckPerms api;
    private final boolean enabled;

    public LuckPermsHook(Main plugin) {
        RegisteredServiceProvider<LuckPerms> provider =
                plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.api = provider.getProvider();
            this.enabled = true;
            plugin.getLogger().info("LuckPerms успешно подключен.");
        } else {
            this.enabled = false;
            plugin.getLogger().warning("LuckPerms не найден. Группы будут недоступны.");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addGroup(Player player, String groupName) {
        if (!enabled) return;
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        InheritanceNode node = InheritanceNode.builder(groupName).build();
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    public Collection<String> getGroups() {
        if (!enabled) return Collections.emptyList();
        return api.getGroupManager().getLoadedGroups()
                .stream()
                .map(g -> g.getName())
                .sorted()
                .collect(Collectors.toList());
    }
}
