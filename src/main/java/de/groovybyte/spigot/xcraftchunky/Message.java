package de.groovybyte.spigot.xcraftchunky;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class Message {

    private static final BaseComponent[] PREFIX = TextComponent.fromLegacyText(
        "§f[§2XcraftChunky§f] §3"
    );

    public static void sendError(CommandSender receiver, String errorMessage) {
        sendMessage(receiver, "§c" + errorMessage);
    }

    public static void sendMessage(CommandSender receiver, String message) {
        receiver.spigot().sendMessage(
            new ComponentBuilder("")
                .append(PREFIX)
                .append(TextComponent.fromLegacyText(message))
                .create()
        );
    }
}
