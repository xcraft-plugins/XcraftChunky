package de.groovybyte.spigot.xcraftchunky.exceptions;

public class NotProtectedException extends ProtectionException {

    public NotProtectedException() {
        super("This chunk is not yet protected");
    }
}
