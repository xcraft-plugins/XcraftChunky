/*
 * Decompiled with CFR 0.139.
 *
 * Could not load the following classes:
 *  com.sk89q.worldguard.bukkit.WorldGuardPlugin
 *  com.sk89q.worldguard.domains.DefaultDomain
 *  com.sk89q.worldguard.protection.ApplicableRegionSet
 *  com.sk89q.worldguard.protection.managers.RegionManager
 *  com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 *  de.xcraft.INemesisI.Library.Manager.XcraftConfigManager
 *  de.xcraft.INemesisI.Library.Manager.XcraftPluginManager
 *  de.xcraft.INemesisI.Library.Message.Messenger
 *  de.xcraft.INemesisI.Library.XcraftPlugin
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Biome
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.PistonMoveReaction
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.xcraft.INemesisI.Chunky.Manager;

import de.xcraft.INemesisI.Chunky.ChunkRegion;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

public class ChunkManager //extends XcraftPluginManager {
{
//    private XcraftChunky plugin;

    //	public WorldGuardPlugin worldguard;
    private Map<String, Map<String, ChunkRegion>> data = new HashMap<String, Map<String, ChunkRegion>>();
    private ScriptEngineManager mgr;
    private ScriptEngine engine;

//    public ChunkManager(XcraftChunky plugin) {
//        super((XcraftPlugin)plugin);
//        this.plugin = plugin;
//        this.data = new HashMap<String, Map<String, ChunkRegion>>();
//        this.worldguard = plugin.getWorldGuard();
//        this.mgr = new ScriptEngineManager();
//        this.engine = this.mgr.getEngineByName("JavaScript");
//    }
//    public XcraftChunky getPlugin() {
//        return this.plugin;
//    }
//	public WorldGuardPlugin getWorldGuard() {
//		return this.worldguard;
//	}

    public Map<String, Map<String, ChunkRegion>> getData() {
        return this.data;
    }

//	public List<ChunkRegion> getApplicableRegions(Player player, Chunk chunk) {
//		BlockFace[] faces;
//		Block[] blocks;
//		String world = chunk.getWorld().getName();
//		if (this.data.get(world) == null) {
//			this.plugin.getMessenger().sendInfo((CommandSender) player, Msg.ERR_WORLD_NOT_ACTIVE.toString(), true);
//			return null;
//		}
//		RegionManager regionManager = this.worldguard.getRegionManager(chunk.getWorld());
//		if (ChunkManager.hasChunkOwner(chunk) && !ChunkManager.getChunkOwner(chunk).startsWith("!")) {
//			this.plugin.getMessenger().sendInfo((CommandSender) player, Msg.ERR_CHUNK_ALREADY_BOUGHT.toString(), true);
//			return null;
//		}
//		for (Block block : blocks = new Block[]{chunk.getBlock(0, 64, 0), chunk.getBlock(15, 64, 0), chunk.getBlock(0, 64, 15), chunk.getBlock(15, 64, 15)}) {
//			ApplicableRegionSet regions = regionManager.getApplicableRegions(block.getLocation());
//			if (regions.size() == 0) {
//				continue;
//			}
//			for (ProtectedRegion region : regions) {
//				if (region.isOwner(player.getName())) {
//					continue;
//				}
//				this.plugin.getMessenger().sendInfo((CommandSender) player, Msg.ERR_REGION_OVERLAPPING.toString(Msg.Replace.NAME(region.getOwners().toPlayersString()), Msg.Replace.REGION(region.getId())), true);
//				return null;
//			}
//		}
//		ArrayList<ChunkRegion> chunkRegions = new ArrayList<ChunkRegion>();
//		for (BlockFace face : faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST}) {
//			ChunkRegion chunkRegion;
//			Chunk relativeChunk = chunk.getBlock(8, 0, 8).getRelative(face, 16).getChunk();
//			if (!ChunkManager.hasChunkOwner(relativeChunk) || (chunkRegion = this.data.get(world).get(ChunkManager.getChunkOwner(relativeChunk))) == null) {
//				continue;
//			}
//			if (chunkRegion.getRegion().isOwner(player.getName())) {
//				if (chunkRegions.contains(chunkRegion)) {
//					continue;
//				}
//				chunkRegions.add(chunkRegion);
//				continue;
//			}
//			this.plugin.getMessenger().sendInfo((CommandSender) player, Msg.ERR_REGION_NEARBY.toString(Msg.Replace.REGION(chunkRegion.getRegion().getId()), Msg.Replace.NAME(chunkRegion.getRegion().getOwners().toPlayersString())), true);
//			return null;
//		}
//		return chunkRegions;
//	}

    public void markChunk(Player player, Chunk chunk) {
        Block[] corners;
        int y = player.getLocation().getBlockY() + 10;
        block0:
        for (Block corner : corners = new Block[]{chunk.getBlock(0, y, 0), chunk.getBlock(0, y,
            15), chunk.getBlock(15, y, 0), chunk.getBlock(15, y, 15)}) {
            for (int i = 0; i < 100; ++i) {
                Block relative = corner.getRelative(BlockFace.DOWN, i);
                if (relative.getType()
                    .equals(Material.AIR) || relative.getPistonMoveReaction()
                    .equals(PistonMoveReaction.BREAK)) {
                    continue;
                }
                relative.getRelative(BlockFace.UP, 1).setType(Material.JACK_O_LANTERN);
                continue block0;
            }
        }
    }

    public void selectRegion(Player player, ChunkRegion region) {
        if (player.isOp() || player.hasPermission("worldedit.selection.pos")) {
            if (region == null) {
                player.performCommand("sel");
            } else {
                player.performCommand("region sel " + region.getRegion().getId());
            }
        }
    }

//	public int getRegionPrice(boolean create) {
//		ConfigManager cManager = (ConfigManager) this.plugin.getConfigManager();
//		if (create) {
//			return cManager.RegionCreate;
//		}
//		return cManager.RegionDelete;
//	}

//	public double getChunkPrice(Player player, ChunkRegion chunkRegion) {
//		ConfigManager cManager = (ConfigManager) this.plugin.getConfigManager();
//		String eqation = cManager.ChunkEquation;
//		try {
//			this.engine.put("c", String.valueOf(chunkRegion.size()));
//			double price = Double.parseDouble(this.engine.eval(eqation).toString());
//			if (price > (double) cManager.ChunkMax) {
//				price = cManager.ChunkMax;
//			} else if (price < (double) cManager.ChunkMin) {
//				price = cManager.ChunkMin;
//			}
//			if (cManager.useMultiplier) {
//				if (cManager.multiplier.containsKey("World:" + player.getWorld().getName())) {
//					price *= (double) cManager.multiplier.get("World:" + player.getWorld().getName()).intValue();
//				}
//				if (cManager.multiplier.containsKey("Biome:" + player.getLocation().getChunk().getBlock(8, 0, 8).getBiome().toString())) {
//					price *= (double) cManager.multiplier.get("Biome:" + player.getLocation().getChunk().getBlock(8, 0, 8).getBiome().toString()).intValue();
//				}
//				if (cManager.multiplier.containsKey("Group:" + this.plugin.getPermission().getPrimaryGroup(player))) {
//					price *= (double) cManager.multiplier.get("Group:" + this.plugin.getPermission().getPrimaryGroup(player)).intValue();
//				}
//			}
//			return price;
//		} catch (ScriptException e) {
//			e.printStackTrace();
//			return Double.MIN_VALUE;
//		}
//	}

    public static void setChunkOwner(JavaPlugin plugin, Chunk chunk, String owner) {
        chunk.getBlock(0, 0, 0)
            .setMetadata("ChunkOwner",
				new FixedMetadataValue(plugin, owner));
    }

    public static void removeChunkOwner(JavaPlugin plugin, Chunk chunk) {
        chunk.getBlock(0, 0, 0).removeMetadata("ChunkOwner", plugin);
    }

    public static boolean isChunkOwner(Chunk chunk, String owner) {
        if (ChunkManager.hasChunkOwner(chunk)) {
            return chunk.getBlock(0, 0, 0)
                .getMetadata("ChunkOwner")
                .get(0).asString().equals(owner);
        }
        return false;
    }

    public static boolean hasChunkOwner(Chunk chunk) {
        return chunk.getBlock(0, 0, 0).hasMetadata("ChunkOwner");
    }

    public static String getChunkOwner(Chunk chunk) {
        return chunk.getBlock(0, 0, 0)
            .getMetadata("ChunkOwner")
            .get(0).asString();
    }

//	public static Chunk getChunkInDirection(Chunk chunk, Direction direction, BlockFace face) {
//		Direction d = direction.getDirectionInFace(face);
//		return chunk.getWorld().getChunkAt(chunk.getX() + d.getModX(), chunk.getZ() + d.getModZ());
//	}
}
