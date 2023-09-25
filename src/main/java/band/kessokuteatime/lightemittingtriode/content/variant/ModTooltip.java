package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithMultilineTooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public enum ModTooltip implements WithMultilineTooltip {
    EMPTY(false, "empty"),

    DYABLE(true, "dyable"),
    DIMMABLE(true, "dimmable"),
    EMITS_LIGHT(true, "emits_light"),

    SHADE(false, "shade"),
    DETECTOR(false, "detector");

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

    public void addMultilineTooltip(Consumer<Text> tooltipContextBuilder, Object... args) {
        addMultilineTooltip(tooltipContextBuilder, text(args));
    }
}
