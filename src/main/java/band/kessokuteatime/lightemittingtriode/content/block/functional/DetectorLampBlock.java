package band.kessokuteatime.lightemittingtriode.content.block.functional;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.base.ChainedActions;
import band.kessokuteatime.lightemittingtriode.content.block.base.tag.Dimmable;
import band.kessokuteatime.lightemittingtriode.content.block.base.tag.Dyable;
import band.kessokuteatime.lightemittingtriode.content.block.functional.base.FacingPowerableLampBlock;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DetectorLampBlock extends FacingPowerableLampBlock implements Dimmable, Dyable {
    public DetectorLampBlock(Wrapper wrapper) {
        super(wrapper.wrapSettings(s -> s
                .luminance(state ->
                        state.get(Properties.POWERED) && !state.get(LightEmittingTriode.Properties.DIM)
                                ? wrapper.luminance()
                                : 0
                )
        ));

        setDefaultState(
                getDefaultState()
                        .with(LightEmittingTriode.Properties.DIM, false)
        );
    }

    protected Box getBoundingBox(BlockState state, BlockPos pos) {
        return VoxelShaper.scaleOnDirection(
                getVoxelShape(state.get(Properties.FACING)),
                state.get(Properties.FACING),
                5
        ).getBoundingBox().offset(pos);
    }

    protected BlockState setRedstoneOutput(BlockState state, int output) {
        return state.with(Properties.POWERED, output > 0);
    }

    protected int getRedstoneOutput(BlockState state) {
        return state.get(Properties.POWERED) ? 1 : 0;
    }

    protected int getRedstoneOutput(BlockState state, World world, BlockPos pos) {
        List<Entity> list = world.getOtherEntities(null, getBoundingBox(state, pos));

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.canAvoidTraps()) continue;
                return 1;
            }
        }

        return 0;
    }

    private void updatePlateState(@Nullable Entity entity, BlockState state, World world, BlockPos pos, int output) {
        int outputInWorld = this.getRedstoneOutput(state, world, pos);
        boolean hasOutput = output > 0, hasOutputInWorld = outputInWorld > 0;

        if (output != outputInWorld) {
            BlockState newState = this.setRedstoneOutput(state, outputInWorld);
            world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
            this.updateNeighbors(state, world, pos);

            world.scheduleBlockRerenderIfNeeded(pos, state, newState);
        }

        if (!hasOutputInWorld && hasOutput) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.1F, 2.5F);
            world.emitGameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (hasOutputInWorld && !hasOutput) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.1F, 2.5F);
            world.emitGameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (hasOutputInWorld)
            world.scheduleBlockTick(pos, this, 22);
    }

    @Override
    public String[] poweredBlockStateSuffixes() {
        return new String[]{};
    }

    @Override
    public DyeColor getDyableFallback() {
        return wrapper().dyeColor();
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return super.ofAnotherColor(state, dyeColor)
                .with(LightEmittingTriode.Properties.DIM, state.get(LightEmittingTriode.Properties.DIM));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LightEmittingTriode.Properties.DIM));
    }

    @Override
    public boolean canMobSpawnInside(BlockState state) {
        return true;
    }

    @Override
    public void onDimmableBroken(WorldAccess world, BlockPos pos, BlockState state) {
        Dimmable.super.onDimmableBroken(world, pos, state);
        super.onBroken(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        updatePlateState(null, state, world, pos, getRedstoneOutput(state));
        return ChainedActions.chain(
                state, world, pos, player, hand, hit,
                super::onUse,
                Dimmable.super::onUse, Dyable.super::onUse
        );
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        int output = getRedstoneOutput(state);
        if (output > 0)
            updatePlateState(null, state, world, pos, output);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient()) return;

        int output = getRedstoneOutput(state);
        if (output == 0)
            updatePlateState(null, state, world, pos, output);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) return;
        if (this.getRedstoneOutput(state) > 0)
            updateNeighbors(state, world, pos);

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getRedstoneOutput(state);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == state.get(Properties.FACING) ? getRedstoneOutput(state) : 0;
    }
}
