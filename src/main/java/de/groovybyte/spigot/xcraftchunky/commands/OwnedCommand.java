package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.ChunkRepository;
import de.groovybyte.spigot.xcraftchunky.Message;
import de.groovybyte.spigot.xcraftchunky.ProtectedChunkRegion;
import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

public class OwnedCommand implements Command {

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        ChunkRepository cr = plugin.getChunkRepository(player.getWorld());
        Set<ProtectedChunkRegion> ownedRegions = cr.getAllRegions()
            .filter(pcr -> pcr.isOwner(player))
            .collect(Collectors.toSet());
        if (ownedRegions.isEmpty()) {
            Message.sendError(player, "You do not own any regions");
        } else {
            Message.sendMessage(player, MessageFormat.format(
                "You own the following {0} regions:\n{1}",
                ownedRegions.size(),
                ownedRegions.stream()
                    .map(pcr -> "§3 - §6" + pcr.getID())
                    .collect(Collectors.joining("\n"))
            ));
        }
        return true;
    }

}
