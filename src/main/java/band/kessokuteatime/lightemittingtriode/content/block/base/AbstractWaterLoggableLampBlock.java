package band.kessokuteatime.lightemittingtriode.content.block.base;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractWaterLoggableLampBlock extends AbstractLampBlock implements Waterloggable {
    protected AbstractWaterLoggableLampBlock(Settings settings, Variant.Wrapper wrapper) {
        super(settings, wrapper);
        setDefaultState(
                getDefaultState()
                        .with(Properties.WATERLOGGED, false)
        );
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return super.ofAnotherColor(state, dyeColor)
                .with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return Objects.requireNonNull(super.getPlacementState(context))
                .with(
                        Properties.WATERLOGGED,
                        context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER
                );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED)
                ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
