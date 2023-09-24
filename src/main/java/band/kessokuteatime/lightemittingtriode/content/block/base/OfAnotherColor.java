package band.kessokuteatime.lightemittingtriode.content.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;

public interface OfAnotherColor {
    BlockState ofAnotherColor(BlockState state, DyeColor dyeColor);
}
