package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LampBlock extends AbstractGlassBlock implements Waterloggable {
    private final Variant.Wrapper wrapper;

    public LampBlock(Variant.Wrapper wrapper) {
        super(
                AbstractBlock.Settings.copy(Blocks.GLASS)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .luminance(state -> state.get(Properties.LIT) ? wrapper.luminance() : 0)
                        .emissiveLighting((state, world, pos) -> state.get(Properties.LIT))
        );

        this.wrapper = wrapper;
        setDefaultState(
                getDefaultState()
                        .with(Properties.LIT, false)
                        .with(Properties.WATERLOGGED, false)
        );
    }

    protected boolean hasPower(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }

    protected boolean isFullBlock() {
        return wrapper.voxelShape().getMax(Direction.Axis.X) - wrapper.voxelShape().getMin(Direction.Axis.X) >= 1
                && wrapper.voxelShape().getMax(Direction.Axis.Y) - wrapper.voxelShape().getMin(Direction.Axis.Y) >= 1
                && wrapper.voxelShape().getMax(Direction.Axis.Z) - wrapper.voxelShape().getMin(Direction.Axis.Z) >= 1;
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return !isFullBlock() && Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return !isFullBlock() && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        return isFullBlock() ? ItemStack.EMPTY : Waterloggable.super.tryDrainFluid(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.LIT, Properties.WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wrapper.voxelShape();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (hasPower(world, pos))
            world.setBlockState(pos, state.with(Properties.LIT, true));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(
                        Properties.WATERLOGGED,
                        !isFullBlock() && ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER
                );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return !isFullBlock() && state.get(Properties.WATERLOGGED)
                ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        if (!world.isClient) {
            boolean lit = state.get(Properties.LIT);

            if (lit != hasPower(world, pos)) {
                if (lit)
                    world.scheduleBlockTick(pos, this, 4);
                else
                    world.setBlockState(pos, state.cycle(Properties.LIT), 2);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if (state.get(Properties.LIT) && !hasPower(world, pos))
            world.setBlockState(pos, state.cycle(Properties.LIT), 2);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        boolean lit = state.get(Properties.LIT);

        if (lit) {
            Vec3d particlePos = wrapper.placeParticle(pos, random, 1);
            world.addParticle(
                    new DustParticleEffect(LET.toColorArrayFloat(wrapper.colorOverlay(true, 1)), 0.85F),
                    particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                    0, 0, 0
            );
        }
    }
}
