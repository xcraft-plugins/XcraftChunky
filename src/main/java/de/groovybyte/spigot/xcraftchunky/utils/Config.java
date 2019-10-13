package de.groovybyte.spigot.xcraftchunky.utils;

import com.google.common.collect.ImmutableMap;
import de.groovybyte.spigot.xcraftchunky.XcraftChunky;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Config {

    public static final Map<String, Object> DEFAULTS = ImmutableMap.<String, Object>builder()
        .put("ignoredRegions", Collections.EMPTY_LIST)
        .put("maximumChunksPerRegion", 0)
        .put("maximumRegionsPerPlayer", 0)
        .build();

    private final File configFile;
    private final FileConfiguration config;

    public Config(XcraftChunky plugin) throws IOException, InvalidConfigurationException {
        configFile = plugin.getDataFolder().toPath().resolve("config.yml").toFile();
        config = plugin.getConfig();
        setDefaults();
        if (configFile.exists()) {
            plugin.getConfig().load(configFile);
        }
        applyValues();
        config.save(configFile);
    }

    private void setDefaults() {
        DEFAULTS.forEach((key, obj) -> config.addDefault(key, obj));
    }

    private void applyValues() {
        DEFAULTS.keySet().forEach(key -> config.set(key, config.get(key)));
    }

    public Configuration getConfig() {
        return config;
    }

    public List<String> getIgnoredRegions() {
        return config.getStringList("ignoredRegions");
    }

    public int getFallbackMaximumChunksPerRegion() {
        return forNegativeReturnMax(config.getInt("maximumChunksPerRegion.default"));
    }

    public int getMaximumChunksPerRegion(Player player) {
        return forNegativeReturnMax(
            XcraftChunky.VAULT.getGroupsOfPlayer(player)
                .mapToInt(group -> config.getInt("maximumChunksPerRegion." + group))
                .max()
                .orElse(getFallbackMaximumChunksPerRegion())
        );
    }

    public int getFallbackMaximumRegionsPerPlayer() {
        return forNegativeReturnMax(config.getInt("maximumRegionsPerPlayer.default"));
    }

    public int getMaximumRegionsPerPlayer(Player player) {
        return forNegativeReturnMax(
            XcraftChunky.VAULT.getGroupsOfPlayer(player)
                .mapToInt(group -> config.getInt("maximumRegionsPerPlayer." + group))
                .max()
                .orElse(getFallbackMaximumRegionsPerPlayer())
        );
    }

    private int forNegativeReturnMax(int i) {
        if (i < 0) {
            return Integer.MAX_VALUE;
        }
        return i;
    }

    private int forNonPositiveReturnMax(int i) {
        if (i < 1) {
            return Integer.MAX_VALUE;
        }
        return i;
    }
}
