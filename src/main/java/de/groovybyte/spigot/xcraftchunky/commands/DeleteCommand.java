package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.ChunkCoordinate;
import de.groovybyte.spigot.xcraftchunky.ChunkRepository;
import de.groovybyte.spigot.xcraftchunky.ProtectedChunkRegion;
import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeleteCommand implements Command {

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        ChunkRepository cr = plugin.getChunkRepository(player.getWorld());
        Optional<ProtectedChunkRegion> opcr;
        if (args.length == 1) {
            // no arguments
            ChunkCoordinate cc = ChunkCoordinate.atBlock(player.getLocation());
            opcr = cr.getRegion(cc);
        } else {
            // region argument given
            String givenId = args[1];
            opcr = cr.getRegion(givenId);
        }
//		if (!opcr.isPresent()) {
//
//		} else {
//			cr.releaseProtection(opcr.get());
//		}
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
