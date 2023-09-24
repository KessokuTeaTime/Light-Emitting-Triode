package band.kessokuteatime.lightemittingtriode.content.block.functional;

import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.functional.base.SpecialFacingPowerableLampBlock;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ButtonLampBlock extends SpecialFacingPowerableLampBlock {
    private final int pressTicks;

    public ButtonLampBlock(Variant.Wrapper wrapper, int pressTicks) {
        super(wrapper);
        this.pressTicks = pressTicks;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShaper.scaleOnDirection(
                super.getOutlineShape(state, world, pos, context),
                getDirection(state),
                state.get(Properties.POWERED) ? 0.5 : 1
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(Properties.POWERED)) return ActionResult.CONSUME;

        powerOn(state, world, pos);
        playClickSound(player, world, pos, true);

        world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

        return ActionResult.success(world.isClient());
    }

    public void powerOn(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(Properties.POWERED, true), Block.NOTIFY_ALL);
        updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, this, pressTicks);
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound(
                powered ? player : null,
                pos, getClickSound(powered),
                SoundCategory.BLOCKS
        );
    }

    protected SoundEvent getClickSound(boolean powered) {
        return powered ? SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON : SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF;
    }

    protected void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
        PersistentProjectileEntity persistentProjectileEntity = world.getNonSpectatingEntities(
                PersistentProjectileEntity.class,
                state.getOutlineShape(world, pos).getBoundingBox().offset(pos)
        ).stream().findFirst().orElse(null);

        boolean entityNonNull = persistentProjectileEntity != null;

        if (entityNonNull != state.get(Properties.POWERED)) {
            world.setBlockState(pos, state.with(Properties.POWERED, entityNonNull), Block.NOTIFY_ALL);

            updateNeighbors(state, world, pos);
            playClickSound(null, world, pos, entityNonNull);

            world.emitGameEvent(
                    persistentProjectileEntity,
                    entityNonNull ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE,
                    pos
            );
        }
        if (entityNonNull)
            world.scheduleBlockTick(pos, this, pressTicks);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) return;
        if (state.get(Properties.POWERED))
            updateNeighbors(state, world, pos);

        super.onStateReplaced(state, world, pos, newState, false);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if (state.get(Properties.POWERED))
            tryPowerWithProjectiles(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (!world.isClient() && !state.get(Properties.POWERED))
            tryPowerWithProjectiles(state, world, pos);
    }
}
