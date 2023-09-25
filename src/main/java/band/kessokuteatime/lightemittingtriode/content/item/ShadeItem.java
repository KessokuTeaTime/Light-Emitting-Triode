package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithMultilineTooltip;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShadeItem extends Item implements WithMultilineTooltip {
    public ShadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        addMultilineTooltip(
                tooltip::add,
                Text.translatable(LightEmittingTriode.idString("tooltip", "shade"))
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getStack();

        if (ModRegistries.BlockTag.DIMMABLES.contains(state.getBlock()) && !state.get(LightEmittingTriode.Properties.DIM)) {
            world.setBlockState(pos, state.with(LightEmittingTriode.Properties.DIM, true));
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (player != null && !player.isCreative()) {
                stack.decrement(1);
                return ActionResult.CONSUME;
            } else return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
