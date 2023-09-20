package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.LET;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.function.Supplier;

public class ColoredBlockItem extends BlockItem {
    private final DyeColor dyeColor;

    public ColoredBlockItem(Block block, Settings settings, DyeColor dyeColor) {
        super(block, settings);
        this.dyeColor = dyeColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public int getDisplayColor() {
        int color = getDyeColor().getFireworkColor();
        int gray = 0x55;
        int r = (color & 0xFF0000) >> 16, g = (color & 0xFF00) >> 8, b = color & 0xFF;

        return (Math.max(gray, r) << 16)
                + (Math.max(gray, g) << 8)
                + Math.max(gray, b);
    }

    private String getParentTranslationKey() {
        return getTranslationKey().replace("_" + dyeColor.getName(), "");
    }

    @Override
    public Text getName() {
        return Text.translatable(
                getParentTranslationKey(),
                Text.translatable(LET.idString("color", dyeColor.getName()))
        ).styled(style -> style.withColor(getDisplayColor()));
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }
}
