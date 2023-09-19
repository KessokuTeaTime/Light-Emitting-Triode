package band.kessokuteatime.lightemittingtriode.util;

import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Describer {
    CEILING("ceiling", "Ceiling"),
    SLAB("slab", "Slab"),
    CLEAR("clear", "Clear"),
    LANTERN("lantern", "Lantern"),
    ALARM("alarm", "Alarm");

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
    final Function<DyeColor, String> convertor = Describer::dyeColorToName;

    Describer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private static String dyeColorToName(DyeColor dyeColor) {
        return Arrays.stream(dyeColor.getName().split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    public String getId(DyeColor dyeColor, Attachment prefix) {
        return id
                + prefix.getId().map(p -> "_" + p).orElse("")
                + "_" + dyeColor.getName();
    }

    public String getName(DyeColor dyeColor, Attachment prefix) {
        return convertor.apply(dyeColor) + " "
                + prefix.getName().map(p -> p + " ").orElse("")
                + name;
    }
}
