package band.kessokuteatime.lightemittingtriode.content.block.functional.base;

import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSpecialFacingPowerableLampBlock extends AbstractPowerableLampBlock {
    public AbstractSpecialFacingPowerableLampBlock(Variant.Wrapper wrapper) {
        super(wrapper);
        setDefaultState(
                getDefaultState()
                        .with(Properties.WALL_MOUNT_LOCATION, WallMountLocation.FLOOR)
                        .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
        );
    }

    public Direction getDirection(BlockState state) {
        return switch (state.get(Properties.WALL_MOUNT_LOCATION)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.get(Properties.HORIZONTAL_FACING);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(Properties.HORIZONTAL_FACING);
        boolean powered = state.get(Properties.POWERED);

        return VoxelShaper.scaleHeight(switch (state.get(Properties.WALL_MOUNT_LOCATION)) {
            case WALL -> VoxelShaper.rotate(wrapper().voxelShape(), Direction.UP, direction);
            case FLOOR -> direction.getAxis() == Direction.Axis.X
                    ? wrapper().voxelShape()
                    : VoxelShaper.swapAroundAxis(wrapper().voxelShape(), Direction.Axis.Y);
            case CEILING -> direction.getAxis() == Direction.Axis.X
                    ? VoxelShaper.rotate(wrapper().voxelShape(), Direction.UP, Direction.DOWN)
                    : VoxelShaper.swapAroundAxis(
                    VoxelShaper.rotate(wrapper().voxelShape(), Direction.UP, Direction.DOWN), Direction.Axis.Y);
        }, powered ? 0.5 : 1);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return WallMountedBlock.canPlaceAt(world, pos, getDirection(state).getOpposite());
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        for (Direction direction : context.getPlacementDirections()) {
            BlockState state = direction.getAxis() == Direction.Axis.Y

                    ? getDefaultState()
                    .with(Properties.WALL_MOUNT_LOCATION, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
                    .with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing())

                    : getDefaultState()
                    .with(Properties.WALL_MOUNT_LOCATION, WallMountLocation.WALL)
                    .with(Properties.HORIZONTAL_FACING, direction.getOpposite());

            if (!state.canPlaceAt(context.getWorld(), context.getBlockPos())) continue;
            return state;
        }

        return super.getPlacementState(context);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (getDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos))
            return Blocks.AIR.getDefaultState();

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWERED) && getDirection(state) == direction ? 15 : 0;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }
}
