package band.kessokuteatime.lightemittingtriode.datagen;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.IOException;
import java.util.Arrays;
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
                translationBuilder.add(dataOutput.getModContainer().findPath("assets/let/lang/en_us.fundamentals.json").orElseThrow());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }

            Arrays.stream(DyeColor.values()).forEach(dyeColor ->
                    translationBuilder.add(LET.idString("color", dyeColor.getName()), formatDyeColor(dyeColor))
            );
        }

        private static String formatDyeColor(DyeColor dyeColor) {
            return Arrays.stream(dyeColor.getName().split("_"))
                    .map(StringUtils::capitalize)
                    .collect(Collectors.joining(" "));
        }
    }

    private static class Model extends FabricModelProvider {
        private Model(FabricDataOutput output) {
            super(output);
        }

        private static final TriConsumer<ItemModelGenerator, Identifier, Item> uploadModelWithParent = (itemModelGenerator, identifier, item) ->
                new net.minecraft.data.client.Model(Optional.of(identifier), Optional.empty())
                        .upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), itemModelGenerator.writer);

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            Arrays.stream(LETRegistries.Blocks.Type.values()).forEach(type ->
                    type.getBlockItemMap().forEach((block, item) -> blockStateModelGenerator.blockStateCollector.accept(
                            MultipartBlockStateSupplier.create(block).with(
                                    When.create().set(Properties.LIT, false),
                                    BlockStateVariant.create().put(VariantSettings.MODEL, type.getIdPack().blockId())
                            ).with(
                                    new When.PropertyCondition().set(Properties.LIT, true),
                                    BlockStateVariant.create().put(VariantSettings.MODEL, type.getIdPack().blockId())
                            )
                    ))
            );
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            Arrays.stream(LETRegistries.Blocks.Type.values()).forEach(type ->
                    type.getBlockItemMap().forEach((block, item) -> uploadModelWithParent.accept(
                            itemModelGenerator, type.getIdPack().blockId(), item
                    ))
            );
        }
    }
}
