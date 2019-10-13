package de.groovybyte.spigot.xcraftchunky.exceptions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlreadyProtectedException extends ProtectionException {

    public AlreadyProtectedException() {
        super("This chunk has already been protected");
    }

    public AlreadyProtectedException(Stream<ProtectedRegion> overlappingRegions) {
        super(
            "This chunk has already been protected - check: "
                + overlappingRegions.map(ProtectedRegion::getId).collect(Collectors.joining(", "))
        );
    }

}
