package band.kessokuteatime.lightemittingtriode;

import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import band.kessokuteatime.lightemittingtriode.util.Util;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
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

    @Override
    public void onInitialize() {
        LETRegistries.register();

        /*
        VoxelShape[] smallDiodeStance = Util.getVariants(4, 0, 4, 12, 1, 12);
        VoxelShape[] largeDiodeStance = Util.getVariants(3, 0, 3, 13, 1, 13);
        VoxelShape[] flatDiodeStance = Util.getVariants(0, 0, 0, 16, 1, 16);

        // Buttons & switches
        LETRegistries.registerForColors("button", DiodeButtonLampBlock::new, DiodeVariant.getButtonRecipe(true));
        LETRegistries.registerForColors("switch", DiodeSwitchLampBlock::new, DiodeVariant.getButtonRecipe(false));

        for (DiodeVariant variant : DiodeVariant.values()) {

            // Full indicator lamp
            LETRegistries.registerForColors(variant.name("clear_full"), () -> new DiodeLampBlock(
                    variant),
                    variant.getRecipe("BBB,BAB,BCB", "clear_full")
            );

            // Small indicator lamp
            LETRegistries.registerForColors(variant.name("small_fixture"), () -> new DirectionalDiodeLampBlock(
                    variant, Util.combineVariants(Util.getVariants( 5, 0, 5, 11, 3, 11 ), smallDiodeStance) ),
                    variant.getRecipe(" B , A ,CCC", "small_fixture")
            );

            // Medium indicator lamp
            LETRegistries.registerForColors(variant.name("medium_fixture"), () -> new DirectionalDiodeLampBlock(
                    variant, Util.combineVariants(Util.getVariants( 5, 0, 5, 11, 8, 11 ), smallDiodeStance) ),
                    variant.getRecipe(" B ,BAB,CCC", "medium_fixture")
            );

            // Large indicator lamp
            LETRegistries.registerForColors(variant.name("large_fixture"), () -> new DirectionalDiodeLampBlock(
                    variant, Util.combineVariants(Util.getVariants( 4, 0, 4, 12, 6, 12 ), largeDiodeStance) ),
                    variant.getRecipe("BBB,BAB,CCC", "large_fixture")
            );

            // Flat indicator lamp
            LETRegistries.registerForColors(variant.name("flat_fixture"), () -> new DirectionalDiodeLampBlock(
                    variant, Util.combineVariants(Util.getVariants( 1, 1, 1, 15, 3, 15 ), flatDiodeStance) ),
                    variant.getRecipe("BBB, A ,CCC", "flat_fixture")
            );
        }

         */
    }
}
