package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.Registries;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ShadeItem extends Item {
    public ShadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getStack();

        if (!state.get(LET.Properties.DIM) && state.isIn(Registries.BlockTags.DIMMABLES)) {
            world.setBlockState(pos, state.with(LET.Properties.DIM, true));
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (player != null && !player.isCreative()) {
                stack.decrement(1);
                return ActionResult.CONSUME;
            } else return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
