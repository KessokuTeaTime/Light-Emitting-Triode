package band.kessokuteatime.lightemittingtriode.block;

import band.kessokuteatime.lightemittingtriode.util.DiodeVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class DiodeLampBlock extends Block {

    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    private final DiodeVariant variant;

    public DiodeLampBlock(DiodeVariant variant) {
        super(variant.settings()
                .luminance((state) -> state.get(LIT) ? variant.getLightLevel() : 0)
                .emissiveLighting((state, world, pos) -> state.get(LIT))
        );

        this.variant = variant;
        setDefaultState(getDefaultState().with(LIT, false));
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        final String text = variant.getTooltip();

        if (text != null) {
            tooltip.add(Text.translatable(text).formatted(Formatting.GRAY));
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
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
