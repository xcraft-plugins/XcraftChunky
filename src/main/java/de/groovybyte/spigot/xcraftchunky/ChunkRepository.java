package de.groovybyte.spigot.xcraftchunky;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import de.groovybyte.spigot.xcraftchunky.exceptions.AlreadyProtectedException;
import de.groovybyte.spigot.xcraftchunky.exceptions.NotConnectedException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkBoundingRect;
import de.groovybyte.spigot.xcraftchunky.utils.WorldGuardUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ChunkRepository {

    private final World world;
    private final RegionManager regMan;
    private final Path savePath;
    private final Logger logger;
    private final Map<ChunkCoordinate, ProtectedChunkRegion> ccRegionMap = new HashMap<>();
    private final Map<String, ProtectedChunkRegion> idRegionMap = new HashMap<>();

    public ChunkRepository(World world, RegionContainer rc, Path savePath, Logger logger) {
        this.world = world;
        Preconditions.checkNotNull(world);
        this.regMan = rc.get(BukkitAdapter.adapt(world));
        Preconditions.checkNotNull(regMan);
        this.savePath = savePath;
        Preconditions.checkNotNull(savePath);
        this.logger = logger;
    }

    private void loadRegion(String id) {
        if (!regMan.hasRegion(id)) {
            logger.log(Level.INFO, "Region vanished: {0}", id);
            return;
        }
        ProtectedRegion pr = regMan.getRegion(id);
        if (pr.getType() != RegionType.POLYGON) {
            logger.log(Level.WARNING, "Invalid region format for region: {0}", id);
            return;
        }
        ProtectedChunkRegion pcr = new ProtectedChunkRegion(pr);
//		logger.log(Level.INFO, "Region loaded: {0}", pcr);
        addRegionChunks(pcr);
    }

    private void addRegionChunks(ProtectedChunkRegion region) {
        region.getChunkRegion().getChunks()
            .forEach(cc -> {
                ccRegionMap.put(cc, region);
//					logger.log(Level.INFO, "Added chunk: {0} for region {1}", new Object[]{cc, region});
            });
    }

    public ChunkRepository saveData() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
            writeData(writer);
            writer.flush();
        }
        return this;
    }

    private void writeData(BufferedWriter writer) {
        logger.log(Level.INFO, "Saving chunk regions for world: {0}", world.getName());
        PrintWriter pw = new PrintWriter(writer);
        getAllRegions()
            .map(ProtectedChunkRegion::getID)
            .forEach(pw::println);
        logger.log(Level.INFO, "{0} regions saved", getAllRegions().count());
        logger.log(Level.INFO, "total of {0} chunks protected", ccRegionMap.size());
    }

    public void loadData(BufferedReader reader) {
        logger.log(Level.INFO, "Loading chunk regions for world: {0}", world.getName());
        ccRegionMap.clear();
        reader.lines().forEach(this::loadRegion);
        logger.log(Level.INFO, "{0} regions loaded", getAllRegions().count());
        logger.log(Level.INFO, "total of {0} chunks protected", ccRegionMap.size());
    }

    public World getWorld() {
        return world;
    }

    public Stream<ProtectedChunkRegion> getAllRegions() {
        return ccRegionMap.values().stream()
            .distinct();
    }

    public Stream<ProtectedChunkRegion> getAllRegionsOwnedBy(Player player) {
        return getAllRegions()
            .filter(pcr -> pcr.isOwner(player));
    }

    public Optional<ProtectedChunkRegion> getRegion(ChunkCoordinate cc) {
        if (!isChunkProtected(cc)) {
            return Optional.empty();
        }
        return Optional.of(ccRegionMap.get(cc));
    }

    public Optional<ProtectedChunkRegion> getRegion(String id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isChunkProtected(ChunkCoordinate cc) {
        ProtectedChunkRegion pcr = ccRegionMap.get(cc);
        if (pcr != null) {
            if (regMan.hasRegion(pcr.getID())) {
                return true;
            } else {
                logger.log(Level.INFO, "Region vanished: {0} - removing chunk protections",
                    pcr.getID());
                releaseProtection(pcr);
            }
        }
        return false;
    }

    public void releaseProtection(ProtectedChunkRegion pcr) {
        pcr.getChunkRegion().getChunks().forEach(ccRegionMap::remove);
        idRegionMap.remove(pcr.getID());

        if (regMan.hasRegion(pcr.getID())) {
            regMan.removeRegion(pcr.getID(), RemovalStrategy.REMOVE_CHILDREN);
        }
    }

    public Stream<ProtectedChunkRegion> findNeighborRegions(ChunkCoordinate cc) {
        return cc.getDirectNeighbors()
            .filter(this::isChunkProtected)
            .map(ccRegionMap::get)
            .distinct();
    }

    public ProtectedChunkRegion appendChunk(ProtectedChunkRegion pcr, ChunkCoordinate cc) throws AlreadyProtectedException, NotConnectedException {
        Optional<ProtectedChunkRegion> opcr = getRegion(cc);
        if (opcr.isPresent()) {
            if (pcr.equals(opcr.get())) {
                throw new AlreadyProtectedException();
            }
        } else {
            logger.log(Level.INFO, "Appending chunk {0} to region {1}", new Object[]{cc, pcr});
            pcr.appendChunk(regMan, cc);
            addRegionChunks(pcr);
            logger.log(Level.INFO, "Region is now: {0}", pcr);
        }
        return pcr;
    }

    public ProtectedChunkRegion claimChunk(ChunkCoordinate cc, Player player) throws AlreadyProtectedException {
        if (isChunkProtected(cc)) {
            throw new AlreadyProtectedException();
        }
        ProtectedChunkRegion pcr = new ProtectedChunkRegion(createRegion(player, cc));
        ccRegionMap.put(cc, pcr);
        return pcr;
    }

    private ProtectedPolygonalRegion createRegion(Player player, ChunkCoordinate cc) throws AlreadyProtectedException {
        String id = WorldGuardUtils.generateNextIDForPlayer(regMan, player);
        LocalPlayer lp = WorldGuardUtils.wrap(player);
        ProtectedPolygonalRegion ppr = new ProtectedPolygonalRegion(
            id,
            cc.asVec2Rectangle(),
            0,
            world.getMaxHeight()
        );
        if (WorldGuardUtils.overlapsProtectedRegions(regMan, ppr)) {
            throw new AlreadyProtectedException();
        }
        ppr.getOwners().addPlayer(player.getUniqueId());
        regMan.addRegion(ppr);
        logger.log(Level.INFO, "Created new region: {0} for player: {1}",
            new Object[]{id, player.getName()});
        return ppr;
    }

    public String chunkStringMap(ChunkBoundingRect bounds) {
        return chunkStringMap(bounds, cc -> "");
    }

    public String chunkStringMap(
        ChunkBoundingRect bounds, Function<ChunkCoordinate, String> colorizer
    ) {
        return bounds
            .expandedBy(1)
            .generateMap(cc -> {
                String color = colorizer.apply(cc);
                String form = bounds.contains(cc) ? "█" : "▒";
                return color + form;
            }, true);
    }
}
