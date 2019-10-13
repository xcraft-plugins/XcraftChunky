package de.groovybyte.spigot.xcraftchunky.utils;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import de.groovybyte.spigot.xcraftchunky.exceptions.AlreadyProtectedException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldGuardUtils {

    public static LocalPlayer wrap(Player player) {
        return WorldGuardPlugin.inst().wrapPlayer(player);
    }

    public static String generateNextIDForPlayer(RegionManager regMan, OfflinePlayer player) {
        String name = player.getName();
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalStateException("unknown player");
        }
        for (int i = 2; regMan.hasRegion(name); i++) {
            name = player.getName() + "_" + i;
        }
        return name;
    }

    public static boolean overlapsProtectedRegions(RegionManager regMan, ProtectedRegion region) {
        return overlapsProtectedRegions(regMan, region, Predicates.alwaysFalse());
    }

    public static boolean overlapsProtectedRegions(
        RegionManager regMan, ProtectedRegion region, Predicate<ProtectedRegion> isIgnored
    ) {
        return overlappingRegions(regMan, region, isIgnored)
            .findAny()
            .isPresent();
    }

    public static Stream<ProtectedRegion> overlappingRegions(
        RegionManager regMan, ProtectedRegion region, Predicate<ProtectedRegion> isIgnored
    ) {
        Predicate<ProtectedRegion> configIsIgnored = WorldGuardUtils::isIgnoredRegion;
        Predicate<ProtectedRegion> globalIsIgnored = isIgnored.or(configIsIgnored);
        return regMan.getApplicableRegions(region)
            .getRegions()
            .stream()
            .filter(globalIsIgnored.negate());
    }

    public static void checkForOverlappingRegions(
        RegionManager regMan, ProtectedRegion region, Predicate<ProtectedRegion> isIgnored
    ) throws AlreadyProtectedException {
        List<ProtectedRegion> overlappingRegions = overlappingRegions(regMan, region, isIgnored)
            .collect(Collectors.toList());
        if (!overlappingRegions.isEmpty()) {
            throw new AlreadyProtectedException(overlappingRegions.stream());
        }
    }

    private static boolean isIgnoredRegion(ProtectedRegion region) {
        return XcraftChunky.CONFIG.getIgnoredRegions().stream()
            .anyMatch(id -> region.getId().equalsIgnoreCase(id));
    }

    public static ProtectedPolygonalRegion regionCopyWithNewPoints(
        ProtectedRegion oldRegion, List<BlockVector2> points
    ) {
        ProtectedPolygonalRegion newRegion = new ProtectedPolygonalRegion(
            oldRegion.getId(),
            points,
            oldRegion.getMinimumPoint().getBlockY(),
            oldRegion.getMaximumPoint().getBlockY()
        );
        newRegion.copyFrom(oldRegion);
        return newRegion;
    }
}
