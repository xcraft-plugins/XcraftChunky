/*
 * Decompiled with CFR 0.139.
 *
 * Could not load the following classes:
 *  com.sk89q.worldedit.BlockVector2D
 *  com.sk89q.worldedit.Vector2D
 *  com.sk89q.worldguard.domains.DefaultDomain
 *  com.sk89q.worldguard.protection.databases.ProtectionDatabaseException
 *  com.sk89q.worldguard.protection.managers.RegionManager
 *  com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion$CircularInheritanceException
 *  de.xcraft.INemesisI.Library.Manager.XcraftConfigManager
 *  de.xcraft.INemesisI.Library.Message.Messenger
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.xcraft.INemesisI.Chunky;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import de.xcraft.INemesisI.Chunky.Manager.ChunkManager;
import org.bukkit.Chunk;

import java.util.List;

public class ChunkRegion {
    //    private XcraftChunky plugin;
    private RegionManager regionManager;
    private List<Chunk> chunks;
    private String owner;
    private int suffix;
    private ProtectedPolygonalRegion region;

//    public ChunkRegion(XcraftChunky plugin, RegionManager regionManager, List<Chunk> chunks, String owner) {
//        this.plugin = plugin;
//        this.regionManager = regionManager;
//        this.chunks = chunks;
//        this.owner = owner.toLowerCase();
//        this.suffix = 1;
//        while (regionManager.getRegion(String.valueOf(owner) + "-" + this.suffix) != null) {
//            ++this.suffix;
//        }
//        this.rebuild();
//    }
//
//    public ChunkRegion(XcraftChunky plugin, RegionManager regionManager, List<Chunk> chunks, String owner, int suffix, ProtectedPolygonalRegion region) {
//        this.plugin = plugin;
//        this.regionManager = regionManager;
//        this.chunks = chunks;
//        this.owner = owner.toLowerCase();
//        this.suffix = suffix;
//        this.region = region;
//        if (region == null) {
//            this.rebuild();
//        } else {
//            this.mark();
//        }
//    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getSuffix() {
        return this.suffix;
    }

    public void setSuffix(int suffix) {
        this.suffix = suffix;
    }

    public ProtectedPolygonalRegion getRegion() {
        return this.region;
    }

    public void setRegion(ProtectedPolygonalRegion region) {
        this.region = region;
    }

    protected List<Chunk> getChunks() {
        return this.chunks;
    }

//    public void add(Chunk chunk) {
//        this.chunks.add(chunk);
//        this.rebuild();
//    }

//    public void combine(ChunkRegion region) {
//        this.chunks.addAll(region.getChunks());
//        this.rebuild();
//    }

//    public void remove(Chunk chunk) {
//        Iterator<Chunk> i = this.chunks.iterator();
//        while (i.hasNext()) {
//            Chunk c = i.next();
//            if (c.getX() != chunk.getX() || c.getZ() != chunk.getZ()) continue;
//            i.remove();
//            break;
//        }
//        ChunkManager.removeChunkOwner((JavaPlugin)this.plugin, chunk);
//        this.rebuild();
//    }

//    public void mark() {
//        String id = String.valueOf(this.owner) + "-" + this.suffix;
//        for (Chunk chunk : this.chunks) {
//            ChunkManager.setChunkOwner((JavaPlugin)this.plugin, chunk, id);
//        }
//    }

//    public void unMark() {
//        for (Chunk chunk : this.chunks) {
//            ChunkManager.removeChunkOwner((JavaPlugin)this.plugin, chunk);
//        }
//    }

    public int size() {
        return this.chunks.size();
    }

//    public boolean touches(Chunk chunk) {
//        int r = 1;
//        this.mark();
//        for (int x = -r; x < r; ++x) {
//            for (int z = -r; z < r; ++z) {
//                Chunk check = chunk.getWorld().getChunkAt(chunk.getX() + x, chunk.getZ() + z);
//                if (!ChunkManager.isChunkOwner(check, this.region.getId())) continue;
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean contains(Chunk chunk) {
        for (Chunk c : this.chunks) {
            if (c.getX() != chunk.getX() || c.getZ() != chunk.getZ()) continue;
            return true;
        }
        return false;
    }

    public boolean isOwner(Chunk chunk) {
        return ChunkManager.isChunkOwner(chunk, this.region.getId());
    }

//    public void rebuild() {
//        long time = System.nanoTime();
//        String regionName = String.valueOf(this.owner) + "-" + this.suffix;
//        this.mark();
//        List<BlockVector2D> points = this.getPoints(this.chunks, regionName);
//        long time2 = System.nanoTime();
//        ProtectedPolygonalRegion newRegion = new ProtectedPolygonalRegion(regionName, points, 0, this.chunks.get(0).getWorld().getMaxHeight());
//        if (this.region != null) {
//            newRegion.setOwners(this.region.getOwners());
//            newRegion.setMembers(this.region.getMembers());
//            newRegion.setFlags(this.region.getFlags());
//            newRegion.setPriority(this.region.getPriority());
//            try {
//                newRegion.setParent(this.region.getParent());
//            }
//            catch (ProtectedRegion.CircularInheritanceException e1) {
//                e1.printStackTrace();
//            }
//        } else {
//            newRegion.getOwners().addPlayer(this.owner);
//        }
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": creating empty region took " + (float)(System.nanoTime() - time2) / 1000000.0f + "ms"));
//        this.validateRegion(newRegion, this.chunks, regionName);
//        time2 = System.nanoTime();
//        this.regionManager.addRegion((ProtectedRegion)newRegion);
//        this.region = newRegion;
//        try {
//            this.regionManager.save();
//            ((ConfigManager)this.plugin.getConfigManager()).saveChunkRegion(this.chunks.get(0).getWorld(), this);
//        }
//        catch (ProtectionDatabaseException e) {
//            Messenger.severe((String)("ERROR while saving regions. " + newRegion.getId() + " was just rebuilt"));
//            e.printStackTrace();
//        }
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": saving took " + (float)(System.nanoTime() - time2) / 1000000.0f + "ms (blame WG)"));
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": Region rebuild took " + (float)(System.nanoTime() - time) / 1000000.0f + "ms with " + this.chunks.size() + " chunks and " + this.region.getPoints().size() + " points!"));
//    }

//    private List<BlockVector2D> getPoints(List<Chunk> chunks, String chunkOwner) {
//        long time = System.nanoTime();
//        ArrayList<BlockVector2D> points = new ArrayList<BlockVector2D>();
//        Chunk chunk = chunks.get(0);
//        BlockFace face = BlockFace.NORTH;
//        for (Chunk c : chunks) {
//            ChunkManager.setChunkOwner((JavaPlugin)this.plugin, c, chunkOwner);
//            if ((c.getZ() < chunk.getZ() || c.getX() <= chunk.getX()) && c.getZ() <= chunk.getZ()) continue;
//            chunk = c;
//        }
//        do {
//            BlockVector2D bv;
//            Chunk check;
//            if (ChunkManager.isChunkOwner(check = ChunkManager.getChunkInDirection(chunk, Direction.FRONTRIGHT, face), chunkOwner)) {
//                Chunk check2 = ChunkManager.getChunkInDirection(chunk, Direction.FRONT, face);
//                if (ChunkManager.isChunkOwner(check2, chunkOwner)) {
//                    bv = this.getBlockVectorAtChunkCorner(check2, Direction.BACKRIGHT, face);
//                    if (points.contains((Object)bv)) break;
//                    points.add(bv);
//                } else {
//                    points.add(this.getBlockVectorAtChunkCorner(chunk, Direction.FRONTRIGHT, face));
//                    points.add(this.getBlockVectorAtChunkCorner(check, Direction.BACKLEFT, face));
//                }
//                chunk = check;
//                face = this.getFaceInDirection(face, Direction.RIGHT);
//                continue;
//            }
//            check = ChunkManager.getChunkInDirection(chunk, Direction.FRONT, face);
//            if (ChunkManager.isChunkOwner(check, chunkOwner)) {
//                chunk = check;
//                continue;
//            }
//            bv = this.getBlockVectorAtChunkCorner(chunk, Direction.FRONTRIGHT, face);
//            if (points.contains((Object)bv)) break;
//            points.add(bv);
//            face = this.getFaceInDirection(face, Direction.LEFT);
//        } while (true);
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": getPoints() call took " + (float)(System.nanoTime() - time) / 1000000.0f + "ms with " + chunks.size() + " chunks and " + points.size() + " points!"));
//        return points;
//    }

//    private void validateRegion(ProtectedPolygonalRegion region, List<Chunk> chunks, String regionName) {
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": starting validation...."));
//        long time = System.nanoTime();
//        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
//        World world = chunks.get(0).getWorld();
//        for (Chunk chunk : chunks) {
//            Block block = chunk.getBlock(8, 0, 8);
//            for (BlockFace face : faces) {
//                Block relativeBlock = block.getRelative(face, 16);
//                Chunk relativeChunk = world.getChunkAt(relativeBlock);
//                if (ChunkManager.isChunkOwner(relativeChunk, regionName) || !region.contains(new BlockVector2D(relativeBlock.getX(), relativeBlock.getZ()))) continue;
//                ArrayList<Chunk> needToUnprotect = new ArrayList<Chunk>();
//                needToUnprotect.add(relativeChunk);
//                ChunkManager.setChunkOwner((JavaPlugin)this.plugin, relativeChunk, "!" + regionName);
//                for (int o = 0; o < needToUnprotect.size(); ++o) {
//                    Chunk c1 = (Chunk)needToUnprotect.get(o);
//                    Block b1 = c1.getBlock(8, 0, 8);
//                    for (BlockFace f : faces) {
//                        Block b2 = b1.getRelative(f, 16);
//                        Chunk c2 = world.getChunkAt(b2);
//                        if (ChunkManager.isChunkOwner(c2, regionName) || needToUnprotect.contains((Object)c2) || !region.contains(new BlockVector2D(b2.getX(), b2.getZ()))) continue;
//                        needToUnprotect.add(c2);
//                        ChunkManager.setChunkOwner((JavaPlugin)this.plugin, c2, "!" + regionName);
//                    }
//                }
//                this.combinePoints(region, this.getPoints(needToUnprotect, "!" + regionName));
//                for (Chunk c : needToUnprotect) {
//                    ChunkManager.removeChunkOwner((JavaPlugin)this.plugin, c);
//                }
//            }
//        }
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": ...validation took " + (float)(System.nanoTime() - time) / 1000000.0f + "ms with " + chunks.size() + " chunks"));
//    }

//    private void combinePoints(ProtectedPolygonalRegion newRegion, List<BlockVector2D> points) {
//        long time = System.nanoTime();
//        BlockVector2D unprotectedCut = points.get(0);
//        BlockVector2D protectedCut = (BlockVector2D)newRegion.getPoints().get(0);
//        double distance = unprotectedCut.distance((Vector2D)protectedCut);
//        for (BlockVector2D up : points) {
//            for (BlockVector2D pp : newRegion.getPoints()) {
//                if (!(up.distance((Vector2D)pp) < distance)) continue;
//                distance = up.distance((Vector2D)pp);
//                unprotectedCut = up;
//                protectedCut = pp;
//            }
//        }
//        int upIndex = points.indexOf((Object)unprotectedCut);
//        int ppIndex = newRegion.getPoints().indexOf((Object)protectedCut);
//        newRegion.getPoints().addAll(ppIndex, points.subList(0, upIndex + 1));
//        newRegion.getPoints().addAll(ppIndex, points.subList(upIndex, points.size()));
//        newRegion.getPoints().add(ppIndex, protectedCut);
//        Messenger.info((String)(String.valueOf(this.plugin.getName()) + ": combinePoints() call took " + (float)(System.nanoTime() - time) / 1000000.0f + "ms with " + newRegion.getPoints().size() + " Points now. (+" + points.size() + ")"));
//    }

//    private BlockFace getFaceInDirection(BlockFace face, Direction dir) {
//        switch (face) {
//            case NORTH: {
//                if (dir == Direction.LEFT) {
//                    return BlockFace.WEST;
//                }
//                if (dir == Direction.RIGHT) {
//                    return BlockFace.EAST;
//                }
//            }
//            case WEST: {
//                if (dir == Direction.LEFT) {
//                    return BlockFace.SOUTH;
//                }
//                if (dir == Direction.RIGHT) {
//                    return BlockFace.NORTH;
//                }
//            }
//            case SOUTH: {
//                if (dir == Direction.LEFT) {
//                    return BlockFace.EAST;
//                }
//                if (dir == Direction.RIGHT) {
//                    return BlockFace.WEST;
//                }
//            }
//            case EAST: {
//                if (dir == Direction.LEFT) {
//                    return BlockFace.NORTH;
//                }
//                if (dir != Direction.RIGHT) break;
//                return BlockFace.SOUTH;
//            }
//        }
//        return null;
//    }

//    private BlockVector2D getBlockVectorAtChunkCorner(Chunk chunk, Direction corner, BlockFace face) {
//        Direction c = corner.getDirectionInFace(face);
//        Block b = chunk.getBlock((int)((double)c.getModX() * 7.5 + 7.5), 0, (int)((double)c.getModZ() * 7.5 + 7.5));
//        return new BlockVector2D(b.getX(), b.getZ());
//    }

    public String toString() {
        String chunkData = "ID: " + this.owner + "-" + this.suffix + " Chunks: ";
        for (Chunk chunk : this.chunks) {
            chunkData = chunkData + chunk.getX() + "," + chunk.getZ() + "; ";
        }
        return chunkData;
    }
}

