package band.kessokuteatime.lightemittingtriode.content.block.functional;

import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.functional.base.FacingPowerableLampBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class SwitchLampBlock extends FacingPowerableLampBlock {
    public SwitchLampBlock(Variant.Wrapper wrapper) {
        super(wrapper);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShaper.scaleHeight(
                super.getOutlineShape(state, world, pos, context),
                state.get(Properties.POWERED) ? 0.5 : 1
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            state = state.cycle(Properties.POWERED);
            if (state.get(Properties.POWERED))
                spawnParticles(state, world, pos, 1);

            return ActionResult.SUCCESS;
        }

        state = togglePower(state, world, pos);
        world.playSound(
                null, pos,
                SoundEvents.BLOCK_LEVER_CLICK,
                SoundCategory.BLOCKS,
                0.3F, state.get(Properties.POWERED) ? 0.6F : 0.5F
        );
        world.emitGameEvent(
                player,
                state.get(Properties.POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE,
                pos
        );

        return ActionResult.CONSUME;
    }

    public BlockState togglePower(BlockState state, World world, BlockPos pos) {
        state = state.cycle(Properties.POWERED);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        updateNeighbors(state, world, pos);

        return state;
    }

    private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
        Direction direction = state.get(Properties.FACING).getOpposite();

        double x = pos.getX() + 0.5 + 0.1 * direction.getOffsetX() + 0.2 * direction.getOffsetX();
        double y = pos.getY() + 0.5 + 0.1 * direction.getOffsetY() + 0.2 * direction.getOffsetY();
        double z = pos.getZ() + 0.5 + 0.1 * direction.getOffsetZ() + 0.2 * direction.getOffsetZ();

        world.addParticle(new DustParticleEffect(DustParticleEffect.RED, alpha), x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (state.get(Properties.POWERED) && random.nextFloat() < 0.25F)
            spawnParticles(state, world, pos, 0.5F);
    }
}
