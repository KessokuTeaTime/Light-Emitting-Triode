package band.kessokuteatime.lightemittingtriode.content.block.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface Dimmable {
    default ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (state.get(LightEmittingTriode.Properties.DIM) && LightEmittingTriode.isValidTool(stack)) {
            world.setBlockState(pos, state.with(LightEmittingTriode.Properties.DIM, false));
            world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.isCreative()) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
                dropShadeStack(world, pos, 1);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    default void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.get(LightEmittingTriode.Properties.DIM))
            dropShadeStack((World) world, pos, 1);
    }

    default void dropShadeStack(World world, BlockPos pos, int count) {
        Block.dropStack(world, pos, new ItemStack(ModRegistries.Items.SHADE, count));
    }
}
