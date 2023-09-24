package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public interface Colorable {
    DyeColor getDyeColor();

    String getParentTranslationKey();

    default int getDisplayColor() {
        return LightEmittingTriode.mapColorRange(getDyeColor().getSignColor(), 0x55, 0);
    }

    default Text getName() {
        return Text.translatable(
                getParentTranslationKey(),
                Text.translatable(LightEmittingTriode.idString("color", getDyeColor().getName()))
        ).styled(style -> style.withColor(getDisplayColor()));
    }
}
