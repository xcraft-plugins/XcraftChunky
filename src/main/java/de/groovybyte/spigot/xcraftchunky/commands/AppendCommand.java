package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.*;
import de.groovybyte.spigot.xcraftchunky.exceptions.ProtectionException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AppendCommand implements Command {

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        ChunkRepository cr = plugin.getChunkRepository(player.getWorld());
        ChunkCoordinate cc = ChunkCoordinate.atBlock(player.getLocation());

        Optional<ProtectedChunkRegion> opcr = cr.getRegion(cc);
        if (opcr.isPresent()) {
            if (opcr.get().isOwner(player)) {
                Message.sendError(player, "You already own this chunk");
            } else {
                Message.sendError(player, "This chunk has already been protected");
            }
            return true;
        }
        try {
            if (args.length == 1) {
                // no arguments
                return appendToAdjacentRegion(plugin, player, cr, cc);
            } else {
                // region argument given
                return appendToGivenRegion(plugin, player, cr, cc, args[1]);
            }
        } catch (ProtectionException ex) {
            Message.sendError(player, ex.getMessage());
            plugin.getLogger().log(Level.INFO, "Error encountered", ex);
            return true;
        }
    }

    public boolean appendToGivenRegion(
        XcraftChunky plugin, Player player,
        ChunkRepository cr, ChunkCoordinate cc, String regionID
    ) {
        // TODO: append to unowned regions if permission granted else show correct error
        Optional<ProtectedChunkRegion> opcr = getOwnedNeighborRegions(player, cr, cc).stream()
            .filter(pcr -> pcr.getID().equalsIgnoreCase(regionID))
            .findAny();
        if (opcr.isPresent()) {
            appendChunk(plugin, player, cr, opcr.get(), cc);
            return true;
        } else {
            Message.sendError(player, "There is no region named \"" + regionID + "\" around here");
            return true;
        }
    }

    public boolean appendToAdjacentRegion(
        XcraftChunky plugin, Player player,
        ChunkRepository cr, ChunkCoordinate cc
    ) {
        List<ProtectedChunkRegion> pcrs = getOwnedNeighborRegions(player, cr, cc);
        try {
            switch (pcrs.size()) {
                case 0:
                    Message.sendError(player, "No adjacent region found");
                    return true;
                case 1:
                    appendChunk(plugin, player, cr, pcrs.get(0), cc);
                    return true;
                default:
                    String regions = pcrs.stream()
                        .map(ProtectedChunkRegion::getID)
                        .collect(Collectors.joining(", "));
                    //TODO: clickable
                    Message.sendMessage(player,
                        "Choose one of your adjacent regions: §6" + regions);
                    return true;
            }
        } catch (ProtectionException ex) {
            Message.sendError(player, ex.getMessage());
            return true;
        }
    }

    private List<ProtectedChunkRegion> getOwnedNeighborRegions(Player player, ChunkRepository cr, ChunkCoordinate cc) {
        return cr.findNeighborRegions(cc)
            .filter(pcr -> pcr.isOwner(player))
            .collect(Collectors.toList());
    }

    private void appendChunk(
        XcraftChunky plugin, Player player,
        ChunkRepository cr, ProtectedChunkRegion pcr, ChunkCoordinate cc
    ) {
        int currentChunkCount = pcr.getChunkRegion().getChunkCount();
        System.out.println(currentChunkCount);
        if (currentChunkCount >= XcraftChunky.CONFIG.getMaximumChunksPerRegion(player)) {
            Message.sendError(player,
                "You reached your chunk limit for this region. You cannot append any further chunks.");
            return;
        }
        cr.appendChunk(pcr, cc);
        ChunkUtils.markChunkBounds(player.getLocation().getChunk());
        plugin.doAsync(() -> {
            //TODO: clickable region id
            Message.sendMessage(player, "Successfully appended this chunk to §6" + pcr.getID());
            plugin.saveWorldData(cr);
        });
    }
}
