package band.kessokuteatime.lightemittingtriode.content.block.functional.base;

import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.block.base.extension.WithCustomBlockModel;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.data.client.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class SpecialFacingPowerableLampBlock extends AbstractPowerableLampBlock implements WithCustomBlockModel, PoweredBlockModelModifiers {
    public SpecialFacingPowerableLampBlock(Wrapper wrapper) {
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
            case WALL -> state.get(Properties.HORIZONTAL_FACING);
        };
    }

    @Override
    public String[] poweredBlockStateSuffixes() {
        return new String[]{ "pressed" };
    }

    @Override
    public BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockModel(ModRegistries.Blocks.Type type) {
        return (blockStateModelGenerator, block) -> VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.POWERED)
                        .register(false, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, wrapper().basis().genericId(poweredBlockStatePrefixes())))
                        .register(true, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, wrapper().basis().genericId(poweredBlockStateSuffixes()))))
                .coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
                        .register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                        .register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        .register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                        .register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create())
                        .register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create()
                                .put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180))
                        .register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180))
                        .register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create()
                                .put(VariantSettings.X, VariantSettings.Rotation.R180))
                        .register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.X, VariantSettings.Rotation.R180)));
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return super.ofAnotherColor(state, dyeColor)
                .with(Properties.WALL_MOUNT_LOCATION, state.get(Properties.WALL_MOUNT_LOCATION))
                .with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(Properties.HORIZONTAL_FACING);

        return switch (state.get(Properties.WALL_MOUNT_LOCATION)) {
            case WALL -> {
                VoxelShape rotated = VoxelShaper.rotate(
                        wrapper().voxelShape(),
                        Direction.UP, direction
                );

                yield direction.getAxis() == Direction.Axis.X
                        ? rotated
                        : VoxelShaper.swapAroundAxis(rotated, Direction.Axis.Z);
            }
            case FLOOR -> direction.getAxis() == Direction.Axis.X
                    ? wrapper().voxelShape()
                    : VoxelShaper.swapAroundAxis(wrapper().voxelShape(), Direction.Axis.Y);
            case CEILING -> {
                VoxelShape rotated = VoxelShaper.rotate(wrapper().voxelShape(), Direction.UP, Direction.DOWN);

                yield direction.getAxis() == Direction.Axis.X
                        ? rotated
                        : VoxelShaper.swapAroundAxis(rotated, Direction.Axis.Y);
            }
        };
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

    protected void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
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
