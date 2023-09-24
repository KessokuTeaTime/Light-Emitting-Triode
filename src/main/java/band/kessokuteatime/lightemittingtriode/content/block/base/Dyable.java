package band.kessokuteatime.lightemittingtriode.content.block.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public interface Dyable extends OfAnotherColor {
    default ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, DyeColor fallback) {
        ItemStack stack = player.getStackInHand(hand);

        if (LightEmittingTriode.isValidDye(stack)) {
            DyeColor dyeColor = LightEmittingTriode.getDyeColorFromDye(stack.getItem(), fallback);
            if (dyeColor == fallback) return ActionResult.PASS;

            BlockState newState = ofAnotherColor(state, dyeColor);
            world.playSound(player, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isClient()) {
                world.setBlockState(pos, newState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, newState));

                if (!player.isCreative())
                    stack.decrement(1);
            }

            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }
}
