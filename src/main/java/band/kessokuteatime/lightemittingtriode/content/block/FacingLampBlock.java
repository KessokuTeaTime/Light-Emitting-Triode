package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.VoxelShapingTool;
import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return switch (type) {
            case AIR -> false;
            case LAND -> state.get(Properties.FACING) == Direction.UP;
            case WATER -> world.getFluidState(pos).isIn(FluidTags.WATER);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.FACING));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(Properties.FACING, ctx.getSide());
    }
}
