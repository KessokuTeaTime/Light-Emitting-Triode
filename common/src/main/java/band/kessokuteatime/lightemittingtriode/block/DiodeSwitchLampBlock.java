package band.kessokuteatime.lightemittingtriode.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DiodeSwitchLampBlock extends DiodeButtonLampBlock {

    public DiodeSwitchLampBlock() {
        super();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // do nothing
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean power = state.get(POWERED);
        world.setBlockState( pos, state.cycle(POWERED) );
        playClickSound(player, world, pos, !power);
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);

        return ActionResult.SUCCESS;
    }

}
