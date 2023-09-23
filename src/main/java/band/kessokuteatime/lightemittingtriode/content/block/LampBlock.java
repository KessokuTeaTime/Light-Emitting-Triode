package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
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

import java.util.function.BiFunction;
import java.util.function.Function;

public class LampBlock extends AbstractGlassBlock implements Waterloggable {
    protected final Variant.Wrapper wrapper;

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
        );
    }

    public BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockStates(LETRegistries.Blocks.Type type) {
        return (blockStateModelGenerator, block) -> VariantsBlockStateSupplier
                .create(block, BlockStateVariant.create().put(VariantSettings.MODEL, type.getIdPack().blockId()));
    }

    protected boolean hasPower(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.LIT));
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
