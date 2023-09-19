package band.kessokuteatime.lightemittingtriode.util;

import band.kessokuteatime.lightemittingtriode.LET;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Describer {
    CEILING("ceiling", "Ceiling"),
    SLAB("slab", "Slab"),
    CLEAR("clear", "Clear"),
    LANTERN("lantern", "Lantern"),
    ALARM("alarm", "Alarm");

    private static final HashMap<String, String> DEFAULT_NAMES = new HashMap<>();

    private static void addDefaultName(String key, String name) {
        DEFAULT_NAMES.put(key, name);
    }

    public static String getDefaultName(String key) {
        if (!DEFAULT_NAMES.containsKey(key)) LET.LOGGER.warn("No default name found for key " + key + ".");
        return DEFAULT_NAMES.getOrDefault(key, "");
    }

    public record Wrapper(Describer describer, Attachment attachment) {
        public String getId(DyeColor dyeColor) {
            return describer().getId(dyeColor, attachment());
        }

        public String getName(DyeColor dyeColor) {
            return describer().getName(dyeColor, attachment());
        }

        public void addToDefaultNames(DyeColor dyeColor) {
            describer().addToDefaultNames(dyeColor, attachment());
        }
    }

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

    Describer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Wrapper with(Attachment attachment) {
        return new Wrapper(this, attachment);
    }

    private static String dyeColorToName(DyeColor dyeColor) {
        return Arrays.stream(dyeColor.getName().split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    public String getId(DyeColor dyeColor, Attachment attachment) {
        return id
                + attachment.getId().map(p -> "_" + p).orElse("")
                + "_" + dyeColor.getName();
    }

    public String getName(DyeColor dyeColor, Attachment attachment) {
        return dyeColorToName(dyeColor) + " "
                + attachment.getName().map(p -> p + " ").orElse("")
                + name;
    }

    public void addToDefaultNames(DyeColor dyeColor, Attachment attachment) {
        addDefaultName(getId(dyeColor, attachment), getName(dyeColor, attachment));
    }
}
