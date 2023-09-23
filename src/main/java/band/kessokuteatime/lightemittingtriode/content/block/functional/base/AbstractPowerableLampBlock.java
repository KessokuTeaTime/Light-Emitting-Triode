package band.kessokuteatime.lightemittingtriode.content.block.functional.base;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractWaterLoggableLampBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public abstract class AbstractPowerableLampBlock extends AbstractWaterLoggableLampBlock {
    protected AbstractPowerableLampBlock(Variant.Wrapper wrapper) {
        super(
                AbstractBlock.Settings.copy(Blocks.GLASS)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .luminance(state -> state.get(Properties.POWERED) ? wrapper.luminance() : 0)
                        .emissiveLighting((state, world, pos) -> state.get(Properties.POWERED)),
                wrapper
        );
        setDefaultState(
                getDefaultState()
                        .with(Properties.POWERED, false)
        );
    }

    @Override
    public boolean isLit(BlockState state) {
        return state.get(Properties.POWERED);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.POWERED));
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWERED) ? 15 : 0;
    }
}
