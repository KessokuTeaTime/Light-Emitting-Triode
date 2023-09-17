package band.kessokuteatime.lightemittingtriode.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public enum DiodeVariant {
    NORMAL("", 15, null, false),
    REINFORCED("reinforced_", 14, "tooltip.led.reinforced", true),
    SHADED("shaded_", 3, "tooltip.led.shaded", false),
    SHADED_REINFORCED("shaded_reinforced_", 3, "tooltip.led.shaded_and_reinforced", true);

    public interface RecipeDelegate {
        void register(Item item, DyeColor color);
    }

    final String prefix;
    final int light;
    final String tooltip;
    final boolean reinforced;

    DiodeVariant(String prefix, int light, String tooltip, boolean reinforced) {
        this.prefix = prefix;
        this.light = light;
        this.tooltip = tooltip;
        this.reinforced = reinforced;
    }

    public FabricBlockSettings settings() {
        return FabricBlockSettings.of(Material.METAL).strength(reinforced ? 0.8f : 0.4f);
    }

    public int getLightLevel() {
        return light;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getName(String name) {
        return prefix + name;
    }

    private static String id(String name) {
        return RegistryHelper.ID + ":" + name;
    }

    private String group(String name) {
        return "led_" + prefix + name;
    }

    public RecipeDelegate getRecipe(String pattern, String name) {
        return switch (this) {
            case NORMAL -> (item, color) -> {
                KeyBuilder keys = new KeyBuilder()
                        .addItem('A', id("led"))
                        .addItem('B', "minecraft:" + color.getName() + "_stained_glass_pane")
                        .addItem('C', "minecraft:iron_nugget");

                RecipeHelper.createShaped(new ItemStack(item), pattern, keys.get(), group(name));
            };

            case REINFORCED -> (item, color) -> RecipeHelper.createShapeless(
                    new ItemStack(item), group(name),
                    "minecraft:iron_bars", id(name + "_" + color.getName())
            );

            case SHADED -> (item, color) -> RecipeHelper.createShapeless(
                    new ItemStack(item), group(name),
                    id("shade"), id(name + "_" + color.getName())
            );

            case SHADED_REINFORCED -> (item, color) -> RecipeHelper.createShapeless(
                    new ItemStack(item), group(name),
                    id("shade"), "minecraft:iron_bars", id(name + "_" + color.getName())
            );
        };
    }

    public static RecipeDelegate getButtonRecipe(boolean button) {
        if (button) {
            return (item, color) -> RecipeHelper.createShapeless(
                    new ItemStack(item, 4), "led_button",
                    id("clear_full_" + color.getName())
            );
        } else {
            return (item, color) -> RecipeHelper.createShapeless(
                    new ItemStack(item), "led_switch",
                    "minecraft:lever", id("button_" + color.getName())
            );
        }
    }


}
