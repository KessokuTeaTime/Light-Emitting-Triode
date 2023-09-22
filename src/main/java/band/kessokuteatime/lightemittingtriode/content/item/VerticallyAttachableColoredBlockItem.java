package band.kessokuteatime.lightemittingtriode.content.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

public class VerticallyAttachableColoredBlockItem extends VerticallyAttachableBlockItem implements Colorable {
    private final DyeColor dyeColor;

    public VerticallyAttachableColoredBlockItem(
            Block standingBlock, Block wallBlock,
            Settings settings, Direction verticalAttachmentDirection,
            DyeColor dyeColor
    ) {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
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
        return Colorable.super.getName(stack);
    }
}
