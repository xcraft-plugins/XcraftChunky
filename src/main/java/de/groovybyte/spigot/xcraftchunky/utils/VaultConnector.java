package de.groovybyte.spigot.xcraftchunky.utils;

import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.stream.Stream;

public class VaultConnector {

    private final Permission permissionProvider;

    public VaultConnector(XcraftChunky plugin) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
            .getRegistration(Permission.class);
        if (rsp == null || rsp.getProvider() == null) {
            throw new IllegalStateException("Missing permission system for vault");
        }
        permissionProvider = rsp.getProvider();
    }

    public Stream<String> getGroupsOfPlayer(Player player) {
        try {
            return Arrays.stream(permissionProvider.getPlayerGroups(player));
        } catch (UnsupportedOperationException ex) {
            return Stream.of("default");
        }
    }
}
