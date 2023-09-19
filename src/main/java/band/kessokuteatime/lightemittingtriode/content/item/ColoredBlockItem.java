package band.kessokuteatime.lightemittingtriode.content.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class ColoredBlockItem extends BlockItem {
    private final int color;

    public ColoredBlockItem(Block block, Settings settings, int color) {
        super(block, settings);
        this.color = Math.max(0, Math.min(0xFFFFFF, color));
    }

    public ColoredBlockItem(Block block, Settings settings, DyeColor dyeColor) {
        super(block, settings);
        this.color = dyeColor.getSignColor();
    }

    public int getColor() {
        return color;
    }

    public int getDisplayColor() {
        int gray = 0x42;
        int r = (color & 0xFF0000) >> 16, g = (color & 0xFF00) >> 8, b = color & 0xFF;

        return (Math.max(gray, r) << 16)
                + (Math.max(gray, g) << 8)
                + Math.max(gray, b);
    }

    @Override
    public Text getName() {
        return ((MutableText) super.getName()).styled(style -> style.withColor(getDisplayColor()));
    }

    @Override
    public Text getName(ItemStack stack) {
        return ((MutableText) super.getName(stack)).styled(style -> style.withColor(getDisplayColor()));
    }
}
