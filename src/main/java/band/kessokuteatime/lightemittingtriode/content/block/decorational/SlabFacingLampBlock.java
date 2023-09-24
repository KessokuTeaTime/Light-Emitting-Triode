package band.kessokuteatime.lightemittingtriode.content.block.decorational;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

public class SlabFacingLampBlock extends FacingLampBlock {
    public SlabFacingLampBlock(Variant.Wrapper wrapper) {
        super(wrapper);
        setDefaultState(
                getDefaultState()
                        .with(LET.Properties.FULL, false)
        );
    }

    @Override
    public BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockModel(ModRegistries.Blocks.Type type) {
        return (blockStateModelGenerator, block) -> VariantsBlockStateSupplier
                .create(block, BlockStateVariant.create().put(VariantSettings.MODEL, type.basis().genericId()))
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(LET.Properties.FULL,
                        ModRegistries.Blocks.Type.CLEAR.basis().genericId(),
                        type.basis().genericId()))
                .coordinate(blockStateModelGenerator.createUpDefaultFacingVariantMap());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LET.Properties.FULL));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return !state.get(LET.Properties.FULL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(LET.Properties.FULL)
                ? VoxelShapes.fullCube()
                : super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        BlockState dryState = context.getWorld().getBlockState(pos);
        if (dryState.isOf(this))
            return dryState
                    .with(LET.Properties.FULL, true)
                    .with(Properties.WATERLOGGED, false);

        FluidState fluidState = context.getWorld().getFluidState(pos);

        return Objects.requireNonNull(super.getPlacementState(context))
                .with(LET.Properties.FULL, false)
                .with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack stack = context.getStack();

        if (state.get(LET.Properties.FULL) ||!stack.isOf(asItem())) return false;

        if (context.canReplaceExisting()) {
            boolean
                    xUpperHalf = context.getHitPos().getX() - context.getBlockPos().getX() >= 0.5,
                    yUpperHalf = context.getHitPos().getY() - context.getBlockPos().getY() >= 0.5,
                    zUpperHalf = context.getHitPos().getZ() - context.getBlockPos().getZ() >= 0.5;

            boolean
                    xLowerHalf = context.getHitPos().getX() - context.getBlockPos().getX() <= 0.5,
                    yLowerHalf = context.getHitPos().getY() - context.getBlockPos().getY() <= 0.5,
                    zLowerHalf = context.getHitPos().getZ() - context.getBlockPos().getZ() <= 0.5;

            Direction direction = context.getSide(), facing = state.get(Properties.FACING);
            
            return facing == direction && switch (facing) {
                case DOWN -> yLowerHalf;
                case UP -> yUpperHalf;
                case NORTH -> zLowerHalf;
                case SOUTH -> zUpperHalf;
                case WEST -> xLowerHalf;
                case EAST -> xUpperHalf;
            };
        }

        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdateFragile(state, direction, neighborState, world, pos, neighborPos, false);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }
}
