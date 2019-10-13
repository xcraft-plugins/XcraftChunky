package de.groovybyte.spigot.xcraftchunky;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.exceptions.AlreadyProtectedException;
import de.groovybyte.spigot.xcraftchunky.utils.ChunkBoundingRect;
import de.groovybyte.spigot.xcraftchunky.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtectedChunkRegion {

    private ConnectedChunkRegion chunkRegion;
    private ProtectedRegion backingProtectedRegion;

    public ProtectedChunkRegion(ProtectedRegion backingRegion) {
        backingProtectedRegion = backingRegion;
        chunkRegion = new ConnectedChunkRegion(backingRegion);
    }

    public String getID() {
        return backingProtectedRegion.getId();
    }

    public ConnectedChunkRegion getChunkRegion() {
        return chunkRegion;
    }

    public ProtectedRegion getBackingRegion() {
        return backingProtectedRegion;
    }

    public ChunkBoundingRect getBounds() {
        return chunkRegion.getBounds();
    }

    public boolean isOwner(Player player) {
        return backingProtectedRegion.getOwners()
            .getPlayerDomain()
            .contains(player.getUniqueId());
    }

    public Stream<OfflinePlayer> getOwners() {
        return backingProtectedRegion.getOwners()
            .getPlayerDomain()
            .getUniqueIds().stream()
            .map(Bukkit::getOfflinePlayer);
    }

    public boolean canBuild(Player player) {
        ApplicableRegionSet ars = new RegionResultSet(Collections.singleton(backingProtectedRegion),
            null);
        return ars.testState(WorldGuardUtils.wrap(player), Flags.BUILD);
    }

    public boolean isSingleChunk() {
//		return chunkRegion.getChunkCount() == 1;
        return chunkRegion.getBounds().isSingleChunk();
    }

    public boolean isProtected(ChunkCoordinate cc) {
        return chunkRegion.contains(cc);
    }

    public void appendChunk(RegionManager regMan, ChunkCoordinate cc) throws AlreadyProtectedException {
        ConnectedChunkRegion ccr = chunkRegion.withChunk(cc);

        ProtectedPolygonalRegion newRegion = WorldGuardUtils.regionCopyWithNewPoints(
            backingProtectedRegion,
            ccr.generatePoints().collect(Collectors.toList())
        );

        WorldGuardUtils.checkForOverlappingRegions(
            regMan, newRegion, Predicate.isEqual(backingProtectedRegion)
        );

        regMan.addRegion(newRegion);
        backingProtectedRegion = newRegion;
        chunkRegion = ccr;
    }

    @Override
    public String toString() {
        return "ProtectedChunkRegion{id=" + getID()
            + ", count=" + chunkRegion.getChunkCount()
            + ", bounds=" + chunkRegion.getBounds().getPrintableString()
            + '}';
    }
}
