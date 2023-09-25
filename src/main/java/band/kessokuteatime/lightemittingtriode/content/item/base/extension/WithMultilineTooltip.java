package band.kessokuteatime.lightemittingtriode.content.item.base.extension;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface WithMultilineTooltip {
    default void addMultilineTooltip(Consumer<Text> tooltipContextBuilder, Text text) {
        String content = text.getString();
        Style style = text.getStyle();
        Matcher matcher = Pattern.compile("\\n|\\r|(\\n\\r)").matcher(content);

        while (matcher.find())
            addTooltip(tooltipContextBuilder, matcher, style, false);

        addTooltip(tooltipContextBuilder, matcher, style, true);
    }

    default void addTooltip(Consumer<Text> tooltipContextBuilder, Matcher matcher, Style style, boolean tail) {
        StringBuilder builder = new StringBuilder();

        if (tail)
            matcher.appendTail(builder);
        else
            matcher.appendReplacement(builder, "");

        tooltipContextBuilder.accept(Text.literal(builder.toString()).fillStyle(style));
    }
}
