package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.LET;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public interface Colorable {
    DyeColor getDyeColor();

    String getParentTranslationKey();

    default int getDisplayColor() {
        return LET.mapColorRange(getDyeColor().getSignColor(), 0x55, 0);
    }

    default Text getName() {
        return Text.translatable(
                getParentTranslationKey(),
                Text.translatable(LET.idString("color", getDyeColor().getName()))
        ).styled(style -> style.withColor(getDisplayColor()));
    }
}
