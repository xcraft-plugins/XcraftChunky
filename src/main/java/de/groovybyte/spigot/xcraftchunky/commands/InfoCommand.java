package de.groovybyte.spigot.xcraftchunky.commands;

import de.groovybyte.spigot.xcraftchunky.*;
import de.groovybyte.spigot.xcraftchunky.exceptions.ProtectionException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Optional;
import java.util.stream.Collectors;

public class InfoCommand implements Command {

    @Override
    public boolean execute(XcraftChunky plugin, Player player, String[] args) {
        ChunkRepository cr = plugin.getChunkRepository(player.getWorld());
        ChunkCoordinate cc = ChunkCoordinate.atBlock(player.getLocation());

        try {
            Optional<ProtectedChunkRegion> opcr = cr.getRegion(cc);
            ProtectedChunkRegion pcr = opcr.get();
            StringBuilder sb = new StringBuilder()
                .append("Region: §6").append(pcr.getID())
                .append(pcr.canBuild(player) ? "§3 - §aYou can build here" : "")
                .append("\n§3Owners: §6").append(
                    pcr.getOwners()
                        .map(p -> Optional.ofNullable(p.getName())
                            .orElse(p.getUniqueId().toString()))
                        .collect(Collectors.joining(", "))
                )
                .append("\n§3Chunk count: §6").append(pcr.getChunkRegion().getChunkCount())
                .append(" §3Chunk");
            if (pcr.isSingleChunk()) {
                sb.append(": §6").append(pcr.getBounds().min.getPrintableString());
            } else {
                sb.append(" bounds: §6").append(pcr.getBounds().getPrintableString());
            }
//			sb.append("\n§r").append(pcr.chunkStringMap());
//			sb.append("\n§r").append(cr.chunkStringMap(
//					pcr.getBounds(),
//					(ChunkCoordinate currentCC) -> {
//						if (currentCC.equals(cc)) {
//							if (pcr.isProtected(currentCC)) {
//								return "§2";
//							} else {
//								return "§6";
//							}
//						} else if (pcr.isProtected(currentCC)) {
//							return "§a";
//						} else if (cr.isChunkProtected(currentCC)) {
//							return "§c";
//						} else {
//							return "§r";
//						}
//					}
//			));
            Message.sendMessage(player, sb.toString());
            return true;
        } catch (ProtectionException ex) {
            Message.sendError(
                player, ex.getMessage()
                //					new StringBuilder(ex.getMessage())
                //					.append("\n§r")
                //					.append(cr.chunkStringMap(new ChunkBoundingRect(cc)))
                //					.toString()
            );
            return true;
        }
    }

    public void highlightCurrentChunk(JavaPlugin plugin, Player player) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        for (int i = 0; i < 5; i++) {
            scheduler.runTaskLater(
                plugin,
                () -> ChunkUtils.highlightChunkBounds(player.getLocation().getChunk()),
                i * 20
            );
        }

    }
}
