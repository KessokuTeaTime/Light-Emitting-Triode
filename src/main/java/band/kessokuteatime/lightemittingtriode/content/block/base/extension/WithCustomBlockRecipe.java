package band.kessokuteatime.lightemittingtriode.content.block.base.extension;

import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public interface WithCustomBlockRecipe {
    Consumer<Consumer<RecipeJsonProvider>> generateRecipe();
}
