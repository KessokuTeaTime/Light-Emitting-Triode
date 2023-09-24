package band.kessokuteatime.lightemittingtriode.content.block.decorational;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractWaterLoggableLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.base.OfAnotherColor;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class LampBlock extends AbstractWaterLoggableLampBlock {
    public LampBlock(Variant.Wrapper wrapper) {
        super(
                AbstractBlock.Settings.copy(Blocks.GLASS)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .luminance(state ->
                                state.get(Properties.LIT) && !state.get(LightEmittingTriode.Properties.DIM)
                                        ? wrapper.luminance()
                                        : 0
                        )
                        .emissiveLighting((state, world, pos) -> state.get(Properties.LIT)),
                wrapper
        );

        setDefaultState(
                getDefaultState()
                        .with(Properties.LIT, false)
                        .with(LightEmittingTriode.Properties.DIM, false)
        );
    }

    protected boolean receivingPower(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return super.ofAnotherColor(state, dyeColor)
                .with(Properties.LIT, state.get(Properties.LIT))
                .with(LightEmittingTriode.Properties.DIM, state.get(LightEmittingTriode.Properties.DIM));
    }

    @Override
    public boolean isLit(BlockState state) {
        return state.get(Properties.LIT);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.LIT, LightEmittingTriode.Properties.DIM));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wrapper().voxelShape();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (receivingPower(world, pos))
            world.setBlockState(pos, state.with(Properties.LIT, true));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);

        if (state.get(LightEmittingTriode.Properties.DIM) && LightEmittingTriode.isValidTool(stack)) {
            world.setBlockState(pos, state.with(LightEmittingTriode.Properties.DIM, false));
            world.playSound(player, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.isCreative())
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));

            return ActionResult.SUCCESS;
        }

        if (LightEmittingTriode.isValidDye(stack)) {
            DyeColor dyeColor = LightEmittingTriode.getDyeColorFromDye(stack.getItem(), wrapper().dyeColor());
            if (dyeColor == wrapper().dyeColor()) return ActionResult.PASS;

            BlockState newState = ofAnotherColor(state, dyeColor);
            world.playSound(player, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isClient()) {
                world.setBlockState(pos, newState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, newState));

                if (!player.isCreative())
                    stack.decrement(1);
            }

            return ActionResult.success(world.isClient);
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        if (!world.isClient) {
            boolean lit = state.get(Properties.LIT);

            if (lit != receivingPower(world, pos)) {
                if (lit)
                    world.scheduleBlockTick(pos, this, 2);
                else
                    world.setBlockState(pos, state.cycle(Properties.LIT), 2);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if (state.get(Properties.LIT) && !receivingPower(world, pos))
            world.setBlockState(pos, state.cycle(Properties.LIT), 2);
    }
}
