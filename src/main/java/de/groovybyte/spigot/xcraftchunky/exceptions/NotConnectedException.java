package de.groovybyte.spigot.xcraftchunky.exceptions;

public class NotConnectedException extends ProtectionException {

    public NotConnectedException() {
        super("This chunk is not connected to your region");
    }

}
