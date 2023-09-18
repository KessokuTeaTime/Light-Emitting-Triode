package band.kessokuteatime.lightemittingtriode.datagen;

import band.kessokuteatime.lightemittingtriode.content.LETRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

import java.io.IOException;

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

            LETRegistries.Blocks.forEach(hashMap -> hashMap.values().forEach(item ->
                    translationBuilder.add(item, LETRegistries.getDefaultName(item))
            ));
        }
    }

    private static class Model extends FabricModelProvider {
        private Model(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            LETRegistries.Blocks.CEILINGS.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );

            LETRegistries.Blocks.SLABS.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
            LETRegistries.Blocks.CLEARS.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );

            LETRegistries.Blocks.LANTERNS_SMALL.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
            LETRegistries.Blocks.LANTERNS.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
            LETRegistries.Blocks.LANTERNS_LARGE.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );

            LETRegistries.Blocks.ALARMS_SMALL.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
            LETRegistries.Blocks.ALARMS.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
            LETRegistries.Blocks.ALARMS_LARGE.forEach((block, item) ->
                    blockStateModelGenerator.registerSimpleCubeAll(block)
            );
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        }
    }
}
