package band.kessokuteatime.lightemittingtriode.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class RecipeHelper {

    public static void createShaped(ItemStack stack, String pattern, JsonObject keys, String group) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        json.add("result", getRecipeResult(stack));
        json.add("pattern", getPattern(pattern));
        json.add("key", keys);
        addGroup(json, group);

        inject(json);
    }

    public static void createShapeless(ItemStack stack, String group, String... items) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        json.add("result", getRecipeResult(stack));
        addGroup(json, group);

        JsonArray ingredients = new JsonArray();
        for (String item : items) {
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", item);
            ingredients.add(ingredient);
        }

        json.add("ingredients", ingredients);
        inject(json);
    }

    private static void addGroup(JsonObject json, String group) {
        if (group != null) {
            json.addProperty("group", group);
        }
    }

    private static JsonObject getRecipeResult( ItemStack stack ) {
        JsonObject json = new JsonObject();
        json.addProperty("item", getIdentifier( stack.getItem() ));
        json.addProperty("count", stack.getCount());
        return json;
    }

    private static JsonArray getPattern(String pattern) {
        JsonArray jsonArray = new JsonArray();
        String[] lines = pattern.split(",");

        for (String line : lines) {
            jsonArray.add(line);
        }

        return jsonArray;
    }

    private static String getIdentifier(Item item) {
        return Registries.ITEM.getId(item).toString();
    }

    private static void inject(JsonObject recipe) {
        Identifier id = new Identifier(recipe.getAsJsonObject("result").getAsJsonPrimitive("item").getAsString());
        RecipeInjector.register(id, recipe);
    }

}
