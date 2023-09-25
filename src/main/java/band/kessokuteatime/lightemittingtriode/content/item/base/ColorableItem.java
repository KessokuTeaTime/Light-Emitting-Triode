package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public interface ColorableItem {
    DyeColor getDyeColor();

    String getParentTranslationKey();

    default int getDisplayColor() {
        return LightEmittingTriode.mapColorRange(getDyeColor().getSignColor(), 0x55, 0);
    }

    default int getDisplayBackgroundColor() {
        return LightEmittingTriode.mapColorRange(getDyeColor().getSignColor(), 0x55, 0xA2);
    }

    default Text getName() {
        return Text.translatable(
                        getParentTranslationKey(),
                        Text.translatable(LightEmittingTriode.idString("color", getDyeColor().getName()))
                )
                .styled(style -> style.withColor(getDisplayColor()));
    }

    default Text getColorTag() {
        return Text.literal(
                "#" +
                        Integer.toHexString(LightEmittingTriode.getColorFromDyeColor(getDyeColor())).toUpperCase()
                )
                .styled(style -> style
                        .withItalic(true)
                        .withColor(getDisplayBackgroundColor())
                );
    }
}
