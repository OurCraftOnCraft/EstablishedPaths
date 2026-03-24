package com.ourcraftoncraft.establishedpaths;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = EstablishedPathsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PathEvents {
    private static final Random RANDOM = new Random();
    private static final double TRANSFORMATION_CHANCE = 0.1; // 1/10 chance
    
    // Track the last block position each player was standing on
    private static final Map<UUID, BlockPos> lastPlayerPositions = new HashMap<>();
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only process on server side, at the end of the tick
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        
        if (!(event.player.level() instanceof ServerLevel level)) {
            return;
        }
        
        Player player = event.player;
        UUID playerId = player.getUUID();
        
        // Get the block position the player is standing on (one block below their feet)
        BlockPos currentPos = player.blockPosition().below();
        
        // Check if player moved to a new block position
        BlockPos lastPos = lastPlayerPositions.get(playerId);
        if (lastPos != null && lastPos.equals(currentPos)) {
            // Player is still on the same block, don't process
            return;
        }
        
        // Update the last position
        lastPlayerPositions.put(playerId, currentPos);
        
        // If this is the first time tracking this player, don't transform yet
        if (lastPos == null) {
            return;
        }
        
        // Check if transformation should occur (1/10 chance)
        if (RANDOM.nextDouble() >= TRANSFORMATION_CHANCE) {
            return;
        }
        
        // Get the block state at the player's feet
        BlockState blockState = level.getBlockState(currentPos);
        
        // Transform the block according to the rules
        BlockState newState = transformBlock(blockState);
        
        if (newState != null && !newState.equals(blockState)) {
            // Set the new block state
            level.setBlock(currentPos, newState, 3);
        }
    }
    
    /**
     * Transforms a block according to the established rules:
     * - Grass block → Dirt
     * - Dirt → Path
     * - Stone → Cobblestone
     * - Cobblestone → Gravel
     * - Coarse Dirt → Gravel
     * - Stone Bricks → Cracked Stone Bricks
     * - Mycelium → Dirt
     * - Nether Brick → Cracked Nether Brick → Netherrack → Air
     * - Soul Sand → Air
     * - Ice → Packed Ice → Blue Ice
     * - Tuff → Gravel
     * - Magma → Cobbled Deepslate
     * 
     * @param currentState The current block state
     * @return The new block state, or null if no transformation should occur
     */
    private static BlockState transformBlock(BlockState currentState) {
        if (currentState.is(Blocks.GRASS_BLOCK)) {
            return Blocks.DIRT.defaultBlockState();
        } else if (currentState.is(Blocks.DIRT)) {
            return Blocks.DIRT_PATH.defaultBlockState();
        } else if (currentState.is(Blocks.STONE)) {
            return Blocks.COBBLESTONE.defaultBlockState();
        } else if (currentState.is(Blocks.COBBLESTONE)) {
            return Blocks.GRAVEL.defaultBlockState();
        } else if (currentState.is(Blocks.COARSE_DIRT)) {
            return Blocks.GRAVEL.defaultBlockState();
        } else if (currentState.is(Blocks.STONE_BRICKS)) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        } else if (currentState.is(Blocks.MYCELIUM)) {
            return Blocks.DIRT.defaultBlockState();
        } else if (currentState.is(Blocks.NETHER_BRICKS)) {
            return Blocks.CRACKED_NETHER_BRICKS.defaultBlockState();
        } else if (currentState.is(Blocks.CRACKED_NETHER_BRICKS)) {
            return Blocks.NETHERRACK.defaultBlockState();
        } else if (currentState.is(Blocks.NETHERRACK)) {
            return Blocks.AIR.defaultBlockState();
        } else if (currentState.is(Blocks.SOUL_SAND)) {
            return Blocks.AIR.defaultBlockState();
        } else if (currentState.is(Blocks.ICE)) {
            return Blocks.PACKED_ICE.defaultBlockState();
        } else if (currentState.is(Blocks.PACKED_ICE)) {
            return Blocks.BLUE_ICE.defaultBlockState();
        } else if (currentState.is(Blocks.TUFF)) {
            return Blocks.GRAVEL.defaultBlockState();
        } else if (currentState.is(Blocks.MAGMA_BLOCK)) {
            return Blocks.COBBLED_DEEPSLATE.defaultBlockState();
        }
        
        return null; // No transformation needed
    }
}

