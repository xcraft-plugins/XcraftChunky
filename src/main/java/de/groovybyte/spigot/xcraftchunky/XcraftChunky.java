package de.groovybyte.spigot.xcraftchunky;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.groovybyte.spigot.xcraftchunky.commands.Commands;
import de.groovybyte.spigot.xcraftchunky.utils.Config;
import de.groovybyte.spigot.xcraftchunky.utils.IORunnable;
import de.groovybyte.spigot.xcraftchunky.utils.VaultConnector;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

public class XcraftChunky extends JavaPlugin implements CommandExecutor, Listener {

    public static Config CONFIG;
    public static VaultConnector VAULT;
    private RegionContainer rgc;
    private Path dataPath;
    private final Map<World, ChunkRepository> worldRepos = new HashMap<>();

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        try {
            CONFIG = new Config(this);
        } catch (IOException | InvalidConfigurationException ex) {
            getLogger().log(Level.SEVERE, "Config failed to load", ex);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        VAULT = new VaultConnector(this);

        rgc = WorldGuard.getInstance().getPlatform().getRegionContainer();

        this.getCommand("chunk").setExecutor(this);

        dataPath = getDataFolder().toPath().resolve("worlds");
        dataPath.toFile().mkdirs();

        for (World world : getServer().getWorlds()) {
            getChunkRepository(world); // preload worlds
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent evt) throws IOException {
        loadWorldData(evt.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent evt) throws IOException {
        worldRepos.remove(evt.getWorld()).saveData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("xcraftchunky.chunkregion.*")) {
                Message.sendError(sender, "You do not have permissions to use this command");
                return true;
            }
            return Commands.executeCommand(this, player, args);
        } else {
            Message.sendError(sender, "Requires player as executor");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Commands.tabcomplete(args);
    }

    ChunkRepository loadWorldData(World world) throws IOException {
        Path path = dataPath.resolve(world.getName() + ".txt");
        if (worldRepos.containsKey(world)) {
            return worldRepos.get(world);
        } else {
            ChunkRepository cr = new ChunkRepository(world, rgc, path, getLogger());
            if (path.toFile().exists()) {
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    cr.loadData(reader);
                }
            }
            worldRepos.put(world, cr);
            return cr;
        }
    }

    public ChunkRepository getChunkRepository(World world) throws IllegalStateException {
        return runSilentIO(() -> loadWorldData(world),
            () -> "Failed to load data for world: " + world.getName());
    }

    @Override
    public void onDisable() {
        for (ChunkRepository cr : worldRepos.values()) {
            saveWorldData(cr);
        }
        worldRepos.clear();
    }

    public void saveWorldData(ChunkRepository cr) {
        runSilentIO(cr::saveData,
            () -> "Failed to save data for world: " + cr.getWorld().getName());
    }

    public void doAsync(Runnable r) {
        getServer().getScheduler().runTaskAsynchronously(this, r);
    }

    public <T> T runSilentIO(IORunnable<T> r, Supplier<String> errorMessage) {
        try {
            return r.run();
        } catch (IOException ex) {
            throw new IllegalStateException(errorMessage.get(), ex);
        }
    }
}
