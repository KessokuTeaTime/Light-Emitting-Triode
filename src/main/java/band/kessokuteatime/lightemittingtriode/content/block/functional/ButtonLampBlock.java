package band.kessokuteatime.lightemittingtriode.content.block.functional;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.functional.base.AbstractSpecialFacingPowerableLampBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.data.client.*;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class ButtonLampBlock extends AbstractSpecialFacingPowerableLampBlock {
    private final int pressTicks;

    public ButtonLampBlock(Variant.Wrapper wrapper, int pressTicks) {
        super(wrapper);
        this.pressTicks = pressTicks;
    }

    @Override
    public Variant.Wrapper wrapper() {
        return wrapper;
    }

    @Override
    public BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockStates(ModRegistries.Blocks.Type type) {
        return (blockStateModelGenerator, block) -> VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.POWERED)
                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, wrapper().basis().genericId()))
                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, wrapper().basis().genericId("pressed"))))
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

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) return;
        if (state.get(Properties.POWERED))
            updateNeighbors(state, world, pos);

        super.onStateReplaced(state, world, pos, newState, false);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
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
