package band.kessokuteatime.lightemittingtriode;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LET implements ModInitializer {
    public static final String ID = "let", NAME = "Light Emitting Triode";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static String idString(String category, String... paths) {
        return category + "." + ID + "." + String.join(".", paths);
    }

    public static MutableText translatable(String category, String... paths) {
        return Text.translatable(idString(category, paths));
    }

    public static Identifier id(String... paths) {
        return new Identifier(ID, String.join("/", paths));
    }

    public static int getColorFromDye(DyeColor dyeColor) {
        return dyeColor.getSignColor();
    }

    public static int mapColorRange(int color, int preOffset, int postOffset) {
        int range = 0xFF - preOffset - postOffset;
        int r = (color & 0xFF0000) >> 16, g = (color & 0xFF00) >> 8, b = color & 0xFF;

        r = (int) (range * ((double) r / 0xFF) + preOffset);
        g = (int) (range * ((double) g / 0xFF) + preOffset);
        b = (int) (range * ((double) b / 0xFF) + preOffset);

        return (MathHelper.clamp(r, 0, 0xFF) << 16) + (MathHelper.clamp(g, 0, 0xFF) << 8) + MathHelper.clamp(b, 0, 0xFF);
    }

    public static Vector3f toColorArrayFloat(int color) {
        return new Vector3f(((color & 0xFF0000) >> 16) / 255F, ((color & 0xFF00) >> 8) / 255F, (color & 0xFF) / 255F);
    }

    public static class Properties {
        public static final BooleanProperty DIM = BooleanProperty.of("dim");
        public static final BooleanProperty FULL = BooleanProperty.of("full");
    }

    @Override
    public void onInitialize() {
        ModRegistries.register();
    }
}
