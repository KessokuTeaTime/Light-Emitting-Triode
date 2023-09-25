package band.kessokuteatime.lightemittingtriode.datagen;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.block.base.extension.WithCustomBlockModel;
import band.kessokuteatime.lightemittingtriode.content.block.base.extension.WithCustomBlockRecipe;
import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithCustomItemParentModelId;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    /**
     * Register {@link DataProvider} with the {@link FabricDataGenerator} during this entrypoint.
     *
     * @param fabricDataGenerator The {@link FabricDataGenerator} instance
     */
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(Language::new);
        pack.addProvider(Model::new);
        pack.addProvider(Recipe::new);
        pack.addProvider(BlockLootTable::new);
        pack.addProvider(ModDataGenerator.BlockTag::new);
    }

    private static class Language extends FabricLanguageProvider {
        protected Language(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        /**
         * Implement this method to register languages.
         *
         * <p>Call {@link TranslationBuilder#add(String, String)} to add a translation.
         */
        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            try {
                // Parse existing English translations
                translationBuilder.add(dataOutput.getModContainer().findPath("assets/let/lang/en_us.fundamentals.json").orElseThrow());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }

            // Add English translations for colors
            Arrays.stream(DyeColor.values()).forEach(dyeColor ->
                    translationBuilder.add(LightEmittingTriode.idString("color", dyeColor.getName()), formatDyeColor(dyeColor))
            );
        }

        private static String formatDyeColor(DyeColor dyeColor) {
            // light_blue -> Light Blue
            return Arrays.stream(dyeColor.getName().split("_"))
                    .map(StringUtils::capitalize)
                    .collect(Collectors.joining(" "));
        }
    }

    private static class Model extends FabricModelProvider {
        private Model(FabricDataOutput output) {
            super(output);
        }

        // Uploads an item model with an existing parent
        private static final TriConsumer<ItemModelGenerator, Identifier, Item> uploadModelWithParent = (itemModelGenerator, identifier, item) ->
                new net.minecraft.data.client.Model(Optional.of(identifier), Optional.empty())
                        .upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), itemModelGenerator.writer);

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            Arrays.stream(ModRegistries.Blocks.Type.values())
                    .forEach(type -> type.wrappers().stream()
                            .map(Wrapper::block)
                            .forEach(block -> blockStateModelGenerator.blockStateCollector.accept(
                                    WithCustomBlockModel.class.isAssignableFrom(block.getClass())
                                            ? ((WithCustomBlockModel) block).generateBlockModel(type).apply(blockStateModelGenerator, block)
                                            : VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, type.basis().genericId()))
                            ))
                    );
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            Arrays.stream(ModRegistries.Blocks.Type.values())
                    .map(ModRegistries.Blocks.Type::wrappers)
                    .flatMap(ArrayList::stream)
                    .forEach(wrapper -> uploadModelWithParent.accept(
                            itemModelGenerator,
                            WithCustomItemParentModelId.class.isAssignableFrom(wrapper.blockItem().getClass())
                                    ? ((WithCustomItemParentModelId) wrapper.blockItem()).getItemModelId(wrapper.basis())
                                    : wrapper.basis().genericId(),
                            wrapper.blockItem()
                    ));
        }
    }

    private static class Recipe extends FabricRecipeProvider {
        public Recipe(FabricDataOutput output) {
            super(output);
        }

        /**
         * Implement this method and then use the range of methods in {@link net.minecraft.data.server.recipe.RecipeProvider}
         * or from one of the recipe json factories such as {@link net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder}
         * or {@link net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder}.
         */
        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            Arrays.stream(ModRegistries.Blocks.Type.values())
                    .forEach(type -> type.wrappers().stream()
                            .map(Wrapper::block)
                            .filter(block -> WithCustomBlockRecipe.class.isAssignableFrom(block.getClass()))
                            .forEach(block -> ((WithCustomBlockRecipe) block).generateRecipe().accept(exporter))
                    );
        }
    }

    private static class BlockLootTable extends FabricBlockLootTableProvider {
        protected BlockLootTable(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        /**
         * Implement this method to add block drops.
         *
         * <p>Use the range of {@link net.minecraft.data.server.loottable.BlockLootTableGenerator#addDrop} methods
         * to generate block drops.
         */
        @Override
        public void generate() {
            // Let blocks drop themselves
            Arrays.stream(ModRegistries.Blocks.Type.values()).forEach(type ->
                    type.wrappers().forEach(wrapper -> addDrop(wrapper.block(), wrapper.blockItem()))
            );
        }
    }

    private static class BlockTag extends FabricTagProvider.BlockTagProvider {
        public BlockTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        /**
         * Implement this method and then use {@link FabricTagProvider#getOrCreateTagBuilder} to get and register new tag builders.
         */
        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            Arrays.stream(ModRegistries.BlockTag.values()).forEach(tag -> addAll(
                    getOrCreateTagBuilder(tag.getTag()),
                    Arrays.stream(ModRegistries.Blocks.Type.values())
                            .map(ModRegistries.Blocks.Type::wrappers)
                            .flatMap(ArrayList::stream)
                            .filter(wrapper -> wrapper.isIn(tag))
                            .toArray(Wrapper[]::new)
            ));
        }

        private void addAll(FabricTagBuilder builder, ModRegistries.Blocks.Type... types) {
            addAll(builder, Arrays.stream(types)
                    .map(ModRegistries.Blocks.Type::wrappers)
                    .flatMap(ArrayList::stream)
                    .toArray(Wrapper[]::new));
        }

        private void addAll(FabricTagBuilder builder, Wrapper... wrappers) {
            Arrays.stream(wrappers)
                    .map(Wrapper::block)
                    .forEach(builder::add);
        }
    }
}
