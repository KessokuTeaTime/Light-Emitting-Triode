package band.kessokuteatime.lightemittingtriode.content.block.functional.base;

import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractWaterLoggableLampBlock;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractPowerableLampBlock extends AbstractWaterLoggableLampBlock {
    protected AbstractPowerableLampBlock(Wrapper wrapper) {
        super(wrapper.wrapSettings(s -> s
                        .luminance(state -> state.get(Properties.POWERED) ? wrapper.luminance() : 0)
                        .emissiveLighting((state, world, pos) -> state.get(Properties.POWERED))
        ));

        setDefaultState(
                getDefaultState()
                        .with(Properties.POWERED, false)
        );
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return super.ofAnotherColor(state, dyeColor)
                .with(Properties.POWERED, state.get(Properties.POWERED));
    }

    @Override
    public boolean isLit(BlockState state) {
        return state.get(Properties.POWERED);
    }

    @Override
    public MutableText getName() {
        return (MutableText) wrapper().blockItem().getName();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.POWERED));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWERED) ? 15 : 0;
    }

    protected abstract void updateNeighbors(BlockState state, World world, BlockPos pos);

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) return;
        if (state.get(Properties.POWERED))
            updateNeighbors(state, world, pos);

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
