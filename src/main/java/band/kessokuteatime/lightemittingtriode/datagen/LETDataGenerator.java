package band.kessokuteatime.lightemittingtriode.datagen;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.VoxelShapingTool;
import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import band.kessokuteatime.lightemittingtriode.content.block.LampBlock;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.*;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LETDataGenerator implements DataGeneratorEntrypoint {
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
        pack.addProvider(BlockLootTable::new);
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
                    translationBuilder.add(LET.idString("color", dyeColor.getName()), formatDyeColor(dyeColor))
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
            // Use specified generators for blocks
            Arrays.stream(LETRegistries.Blocks.Type.values())
                    .forEach(type -> type.getBlockItemMap()
                            .forEach((block, item) -> blockStateModelGenerator.blockStateCollector.accept(
                                    ((LampBlock) block).generateBlockStates(type).apply(blockStateModelGenerator, block)
                            ))
                    );
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            // Use generic block ids for items
            Arrays.stream(LETRegistries.Blocks.Type.values()).forEach(type ->
                    type.getBlockItemMap().forEach((block, blockItem) -> uploadModelWithParent.accept(
                            itemModelGenerator, type.getIdPack().blockId(), blockItem
                    ))
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
            Arrays.stream(LETRegistries.Blocks.Type.values()).forEach(type ->
                    type.getBlockItemMap().forEach(this::addDrop)
            );
        }
    }
}
