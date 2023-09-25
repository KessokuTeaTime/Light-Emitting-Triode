package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public enum ModTooltip {
    DYABLE(true, "dyable"),
    DIMMABLE(true, "dimmable"),

    SHADE(false, "shade"),
    SWITCH(false, "switch"),
    BUTTON(false, "button");

    final String translationKey;

    ModTooltip(boolean featured, String... paths) {
        this.translationKey = LightEmittingTriode.tooltipKey(featured, paths);
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public MutableText text(Object... args) {
        return Text.translatable(getTranslationKey(), args);
    }
}
