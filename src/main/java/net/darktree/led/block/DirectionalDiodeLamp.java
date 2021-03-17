package net.darktree.led.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DirectionalDiodeLamp extends DiodeLamp {

    protected static final DirectionProperty FACING = Properties.FACING;

    public DirectionalDiodeLamp(Settings settings, int light, boolean shaded) {
        super(settings, light, shaded);
        setDefaultState( getDefaultState().with(FACING, Direction.NORTH) );
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess worldAccess, BlockPos pos, BlockPos posFrom) {
        Direction facing = state.get(FACING);
        if( facing != direction || isDirectionValid(facing, worldAccess, pos) ) {
            return state;
        }

        return Blocks.AIR.getDefaultState();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {

        Direction direction = ctx.getPlacementDirections()[0];
        WorldAccess worldAccess = ctx.getWorld();

        if( isDirectionValid(direction, worldAccess, ctx.getBlockPos()) ) {
            return getDefaultState().with( FACING, direction );
        }

        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape(state, world, pos, context);
    }

//    @Override
//    protected boolean hasPower(BlockState state, World world, BlockPos pos) {
//        Direction direction = state.get(FACING);
//        return world.getEmittedRedstonePower(pos.offset(direction), direction) > 0;
//    }

    private boolean isDirectionValid( Direction direction, WorldAccess world, BlockPos pos ) {
        return world.getBlockState(pos.offset(direction)).isSideSolidFullSquare( world, pos, direction.getOpposite() );
    }

}
