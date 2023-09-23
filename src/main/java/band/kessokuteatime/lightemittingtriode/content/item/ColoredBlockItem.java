package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.item.base.Colorable;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColoredBlockItem extends BlockItem implements Colorable {
    private final DyeColor dyeColor;

    public ColoredBlockItem(Block block, Settings settings, DyeColor dyeColor) {
        super(block, settings);
        this.dyeColor = dyeColor;
    }

    @Override
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public String getParentTranslationKey() {
        return getTranslationKey().replace("_" + dyeColor.getName(), "");
    }

    @Override
    public Text getName() {
        return Colorable.super.getName();
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(
                Text.literal("#" + Integer.toHexString(LET.getColorFromDye(dyeColor)).toUpperCase())
                        .formatted(Formatting.DARK_GRAY)
        );
    }
}
