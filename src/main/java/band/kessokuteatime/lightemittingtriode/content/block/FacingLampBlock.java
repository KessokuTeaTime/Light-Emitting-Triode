package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.VoxelShapingTool;
import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
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

import java.util.Objects;
import java.util.function.BiFunction;

public class FacingLampBlock extends WaterLoggableLampBlock {
    public FacingLampBlock(Variant.Wrapper wrapper) {
        super(wrapper);
        setDefaultState(
                getDefaultState()
                        .with(Properties.FACING, Direction.UP)
        );
    }

    @Override
    public BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockStates(LETRegistries.Blocks.Type type) {
        return (blockStateModelGenerator, block) -> VariantsBlockStateSupplier
                .create(block, BlockStateVariant.create().put(VariantSettings.MODEL, type.getIdPack().blockId()))
                .coordinate(blockStateModelGenerator.createUpDefaultFacingVariantMap());
    }

    protected VoxelShape getVoxelShape(Direction direction) {
        return VoxelShapingTool.rotateVoxelShape(wrapper.voxelShape(), Direction.UP, direction);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getVoxelShape(state.get(Properties.FACING));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(Properties.FACING, mirror.apply(state.get(Properties.FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.FACING));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        return Objects.requireNonNull(super.getPlacementState(context))
                .with(Properties.FACING, context.getSide());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return getStateForNeighborUpdateFragile(state, direction, neighborState, world, pos, neighborPos, true);
    }

    public BlockState getStateForNeighborUpdateFragile(
            BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos,
            boolean fragile
    ) {
        return fragile && direction == state.get(Properties.FACING).getOpposite() && !this.canPlaceAt(state, world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return switch (state.get(Properties.FACING)) {
            case DOWN -> Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN);
            case UP -> Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
            case NORTH -> Block.sideCoversSmallSquare(world, pos.south(), Direction.NORTH);
            case SOUTH -> Block.sideCoversSmallSquare(world, pos.north(), Direction.SOUTH);
            case WEST -> Block.sideCoversSmallSquare(world, pos.east(), Direction.WEST);
            case EAST -> Block.sideCoversSmallSquare(world, pos.west(), Direction.EAST);
        };
    }
}
