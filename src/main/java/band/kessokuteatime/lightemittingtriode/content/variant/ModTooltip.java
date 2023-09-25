package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public enum ModTooltip {
    DYABLE("dyable"),
    DIMMABLE("dimmable");

    final String translationKey;

    ModTooltip(String... paths) {
        this.translationKey = LightEmittingTriode.idString("tooltip", paths);
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public MutableText text(Object... args) {
        return Text.translatable(getTranslationKey(), args);
    }
}
