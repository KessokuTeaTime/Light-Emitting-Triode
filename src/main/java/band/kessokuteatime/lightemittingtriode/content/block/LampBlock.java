package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LampBlock extends AbstractGlassBlock {
    private final Variant.Wrapper wrapper;

    public LampBlock(Variant.Wrapper wrapper) {
        super(
                AbstractBlock.Settings.copy(Blocks.GLASS)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .luminance(state -> state.get(Properties.LIT) ? wrapper.luminance() : 0)
                        .emissiveLighting((state, world, pos) -> state.get(Properties.LIT))
        );

        this.wrapper = wrapper;
        setDefaultState(getDefaultState().with(Properties.LIT, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wrapper.voxelShape();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        boolean power = hasPower(world, pos);

        if (power) world.setBlockState(pos, state.with(Properties.LIT, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.LIT);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(Properties.LIT) && !hasPower(world, pos))
            world.setBlockState(pos, state.cycle(Properties.LIT), 2);
    }

    protected boolean hasPower(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
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
