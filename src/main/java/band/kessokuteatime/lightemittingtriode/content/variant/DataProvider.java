package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.AbstractBlock;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataProvider {
    private final Supplier<AbstractBlock.Settings> settingsSupplier;
    private final Supplier<List<ModRegistries.BlockTag>> tagSupplier;
    private @Nullable final Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder;
    private @Nullable final Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder;
    private @Nullable final BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder> recoloringRecipeJsonBuilder;

    public static final BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder> DEFAULT_RECOLORING_RECIPE_JSON_BUILDER =
            (ingredient, wrapper) ->
                    SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    ingredient, Ingredient.ofItems(wrapper.dye()),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(wrapper.basis(), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye()));

    public DataProvider(
            Supplier<AbstractBlock.Settings> settingsSupplier,
            Supplier<List<ModRegistries.BlockTag>> tagSupplier,
            @Nullable Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder,
            @Nullable Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder,
            @Nullable BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder> recoloringRecipeJsonBuilder
    ) {
        this.settingsSupplier = settingsSupplier;
        this.tagSupplier = tagSupplier;
        this.craftingRecipeJsonBuilder = craftingRecipeJsonBuilder;
        this.upgradingRecipeJsonBuilder = upgradingRecipeJsonBuilder;
        this.recoloringRecipeJsonBuilder = recoloringRecipeJsonBuilder;
    }

    public DataProvider(
            Supplier<AbstractBlock.Settings> settingsSupplier,
            Supplier<List<ModRegistries.BlockTag>> tagSupplier,
            @Nullable Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder,
            @Nullable Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder
    ) {
        this(
                settingsSupplier, tagSupplier,
                craftingRecipeJsonBuilder, upgradingRecipeJsonBuilder,
                DEFAULT_RECOLORING_RECIPE_JSON_BUILDER
        );
    }

    public Supplier<AbstractBlock.Settings> settingsSupplier() {
        return settingsSupplier;
    }

    public Supplier<List<ModRegistries.BlockTag>> tagSupplier() {
        return tagSupplier;
    }

    public Optional<Function<Wrapper, CraftingRecipeJsonBuilder>> craftingRecipeJsonBuilder() {
        return Optional.ofNullable(craftingRecipeJsonBuilder);
    }

    public Optional<Function<Wrapper, SmithingTransformRecipeJsonBuilder>> upgradingRecipeJsonBuilder() {
        return Optional.ofNullable(upgradingRecipeJsonBuilder);
    }

    public Optional<BiFunction<Ingredient, Wrapper, SmithingTransformRecipeJsonBuilder>> recoloringRecipeJsonBuilder() {
        return Optional.ofNullable(recoloringRecipeJsonBuilder);
    }
}
