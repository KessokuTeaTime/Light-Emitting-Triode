package band.kessokuteatime.lightemittingtriode.content.block;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LampBlock extends AbstractGlassBlock {
    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    private final Variant.Wrapper wrapper;

    public LampBlock(Variant.Wrapper wrapper) {
        super(
                AbstractBlock.Settings.copy(Blocks.GLASS)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .luminance(state -> state.get(LIT) ? wrapper.luminance() : 0)
                        .emissiveLighting((state, world, pos) -> state.get(LIT))
        );

        this.wrapper = wrapper;
        setDefaultState(getDefaultState().with(LIT, false));
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wrapper.voxelShape();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        boolean power = hasPower(world, pos);

        if (power) {
            world.setBlockState(pos, state.with(LIT, true));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean lit = state.get(LIT);

            if (lit != hasPower(world, pos)) {
                if (lit) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.cycle(LIT), 2);
                }
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT) && !hasPower(world, pos)) {
            world.setBlockState(pos, state.cycle(LIT), 2);
        }
    }

    protected boolean hasPower(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }
}
