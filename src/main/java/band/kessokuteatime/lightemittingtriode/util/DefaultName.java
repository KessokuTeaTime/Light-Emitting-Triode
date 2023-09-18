package band.kessokuteatime.lightemittingtriode.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DefaultName {
    CEILING("ceiling", "Ceiling"),
    SLAB("slab", "Slab"),
    CLEAR("clear", "Clear"),
    LANTERN("lantern", "Lantern"),
    ALARM("alarm", "Alarm");

    private static final HashMap<DyeColor, Character> DYE_COLOR_FORMATTING_MAP = new HashMap<>(Map.ofEntries(
            new AbstractMap.SimpleEntry<>(DyeColor.WHITE, 'f'),
            new AbstractMap.SimpleEntry<>(DyeColor.ORANGE, '6'),
            new AbstractMap.SimpleEntry<>(DyeColor.MAGENTA, 'd'),
            new AbstractMap.SimpleEntry<>(DyeColor.LIGHT_BLUE, 'b'),
            new AbstractMap.SimpleEntry<>(DyeColor.YELLOW, 'e'),
            new AbstractMap.SimpleEntry<>(DyeColor.LIME, 'a'),
            new AbstractMap.SimpleEntry<>(DyeColor.PINK, 'd'),
            new AbstractMap.SimpleEntry<>(DyeColor.GRAY, '8'),
            new AbstractMap.SimpleEntry<>(DyeColor.LIGHT_GRAY, '7'),
            new AbstractMap.SimpleEntry<>(DyeColor.CYAN, 'b'),
            new AbstractMap.SimpleEntry<>(DyeColor.PURPLE, '5'),
            new AbstractMap.SimpleEntry<>(DyeColor.BLUE, '9'),
            new AbstractMap.SimpleEntry<>(DyeColor.BROWN, '6'),
            new AbstractMap.SimpleEntry<>(DyeColor.GREEN, '2'),
            new AbstractMap.SimpleEntry<>(DyeColor.RED, 'c'),
            new AbstractMap.SimpleEntry<>(DyeColor.BLACK, '7')
    ));

    public enum Attachment {
        NONE(null, null),
        SMALL("small", "Small"),
        LARGE("large", "Large");

        final @Nullable String id, name;

        Attachment(@Nullable String prefixId, @Nullable String prefixName) {
            this.id = prefixId;
            this.name = prefixName;
        }

        public Optional<String> getId() {
            return id != null ? Optional.of(id) : Optional.empty();
        }

        public Optional<String> getName() {
            return name != null ? Optional.of(name) : Optional.empty();
        }
    }

    final String id, name;
    final Function<DyeColor, String> convertor = DefaultName::dyeColorToName;

    DefaultName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private static String dyeColorToName(DyeColor dyeColor) {
        return Arrays.stream(dyeColor.getName().split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    private static String dyeColorToFormatting(DyeColor dyeColor) {
        return "§" + DYE_COLOR_FORMATTING_MAP.get(dyeColor);
    }

    public String getId(DyeColor dyeColor, Attachment prefix) {
        return dyeColor.getName() + "_"
                + id
                + prefix.getId().map(p -> "_" + p).orElse("");
    }

    public String getName(DyeColor dyeColor, Attachment prefix) {
        return dyeColorToFormatting(dyeColor)
                + convertor.apply(dyeColor) + " "
                + prefix.getName().map(p -> p + " ").orElse("")
                + name;
    }
}
