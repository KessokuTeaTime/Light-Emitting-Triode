package band.kessokuteatime.lightemittingtriode.content.block.decorational;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractWaterLoggableLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.base.Dimmable;
import band.kessokuteatime.lightemittingtriode.content.block.base.Dyable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.WorldAccess;

public class LampBlock extends AbstractWaterLoggableLampBlock implements Dimmable, Dyable {
    public LampBlock(Variant.Wrapper wrapper) {
        super(wrapper.wrapSettings(s -> s
                .luminance(state ->
                        state.get(Properties.LIT) && !state.get(LightEmittingTriode.Properties.DIM)
                                ? wrapper.luminance()
                                : 0
                )
                .emissiveLighting((state, world, pos) -> state.get(Properties.LIT))
        ));

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
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        Dimmable.super.onBroken(world, pos, state);
        super.onBroken(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Dimmable.super.onUse(state, world, pos, player, hand);
        Dyable.super.onUse(state, world, pos, player, hand, wrapper().dyeColor());

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
