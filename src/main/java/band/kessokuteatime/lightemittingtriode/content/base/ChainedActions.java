package band.kessokuteatime.lightemittingtriode.content.base;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ChainedActions {
    @FunctionalInterface
    interface Action {
        ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);
    }

    static ActionResult chain(
            BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
            Action fallback,
            Action... actions
    ) {
        for (Action action : actions) {
            if (action.onUse(state, world, pos, player, hand, hit).isAccepted())
                return ActionResult.SUCCESS;
        }

        return fallback.onUse(state, world, pos, player, hand, hit);
    }
}
