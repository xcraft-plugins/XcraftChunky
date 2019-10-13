package de.groovybyte.spigot.xcraftchunky.utils;

import java.io.IOException;

@FunctionalInterface
public interface IORunnable<T> {

    T run() throws IOException;
}
