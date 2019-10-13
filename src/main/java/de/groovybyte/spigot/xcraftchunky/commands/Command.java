package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface Command {

    boolean execute(XcraftChunky plugin, Player player, String[] args);
}
