package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.*;
import de.groovybyte.spigot.xcraftchunky.exceptions.ProtectionException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkUtils;
import org.bukkit.entity.Player;

public class ClaimCommand implements Command {

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        ChunkRepository cr = plugin.getChunkRepository(player.getWorld());
        int currentRegionCount = (int) cr.getAllRegionsOwnedBy(player).count();
        if (currentRegionCount >= XcraftChunky.CONFIG.getMaximumRegionsPerPlayer(player)) {
            Message.sendError(player,
                "You reached your region limit for this world. You cannot create any further regions.");
            return true;
        }
        ChunkCoordinate cc = ChunkCoordinate.atBlock(player.getLocation());
        try {
            ProtectedChunkRegion pcr = cr.claimChunk(cc, player);
            ChunkUtils.markChunkBounds(player.getLocation().getChunk());
            plugin.doAsync(() -> {
                //TODO: clickable region id
                Message.sendMessage(player, "Chunk claimed as §6" + pcr.getID());
                plugin.saveWorldData(cr);
            });
            return true;
        } catch (ProtectionException ex) {
            Message.sendError(player, ex.getMessage());
//			plugin.getLogger().log(Level.INFO, "Error encountered", ex);
            return true;
        }
    }

}
