package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.Message;
import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum Commands implements Command {
    OWNED(new OwnedCommand()),
    INFO(new InfoCommand()),
    CLAIM(new ClaimCommand()),
    APPEND(new AppendCommand()),
    REMOVE(new RemoveCommand()),
    DELETE(new DeleteCommand()),
//	SELECT((XcraftChunky plugin, Player player, String[] args) -> {
//		throw new UnsupportedOperationException("Not yet implemented");
//	})
    ;

    private final Command onCmd;

    Commands(Command onCmd) {
        this.onCmd = onCmd;
    }

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        String command = args[0].trim().toLowerCase();
        if (player.hasPermission("xcraftchunky.chunkregion." + command)) {
            return onCmd.execute(plugin, player, args);
        } else {
            Message.sendError(player,
                "You do not have sufficient permissions to run this command.");
            return false;
        }
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }

    public static boolean executeCommand(XcraftChunky plugin, Player player, String[] args) {
        if (args.length == 0) {
            return showHelp(player, args);
        }
        try {
            String command = args[0].trim().toUpperCase();
            return valueOf(command).execute(plugin, player, args);
        } catch (IllegalArgumentException ex) {
            return showHelp(player, args);
        }
    }

    public static List<String> tabcomplete(String[] args) {
        if (args.length <= 1) {
            String subCmd = args.length == 0 ? "" : args[0];
            return Arrays.stream(values())
                .map(Commands::toString)
                .filter(s -> s.startsWith(subCmd))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static boolean showHelp(Player player, String[] args) {
        String cmds = Arrays.stream(Commands.values())
            .map(Commands::name)
            .map(String::toLowerCase)
            .collect(Collectors.joining("/"));
        Message.sendMessage(player, "Use /chunk §6" + cmds);
        return false;
    }
}
