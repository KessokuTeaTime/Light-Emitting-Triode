package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import band.kessokuteatime.lightemittingtriode.util.Describer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LETRegistries {
    private static <B extends Block> B registerBlock(Identifier id, B block) {
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static <I extends Item> I registerItem(Identifier id, I item) {
        return Registry.register(Registries.ITEM, id, item);
    }

    public static class ItemGroups {
        public static final ItemGroup GENERAL = Registry.register(
                Registries.ITEM_GROUP,
                LET.id("general"),
                FabricItemGroup.builder()
                        .icon(Items.LET::getDefaultStack)
                        .displayName(LET.translatable("itemGroup", "general"))
                        .build()
        );

        public static void establish() {
            Registries.ITEM_GROUP.getKey(GENERAL).ifPresent(key ->
                    ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
                        fill(entries, Items.BULB, Items.LED, Items.LET, Items.SHADE);

                        Blocks.forEach((block, item) -> entries.add(item));
                    })
            );
        }

        private static void fill(ItemGroup.Entries entries, ItemStack... itemStacks) {
            for (var itemStack : itemStacks) entries.add(itemStack);
        }

        private static void fill(ItemGroup.Entries entries, ItemConvertible... items) {
            for (var item : items) entries.add(item);
        }
    }

    public static class Blocks {
        public static final HashMap<Block, Item>
                CEILINGS = new HashMap<>(),
                SLABS = new HashMap<>(), CLEARS = new HashMap<>(),
                LANTERNS_SMALL = new HashMap<>(), LANTERNS = new HashMap<>(), LANTERNS_LARGE = new HashMap<>(),
                ALARMS_SMALL = new HashMap<>(), ALARMS = new HashMap<>(), ALARMS_LARGE = new HashMap<>();

        public static void forEach(BiConsumer<Block, Item> consumer) {
            CEILINGS.forEach(consumer);

            SLABS.forEach(consumer);
            CLEARS.forEach(consumer);

            LANTERNS_SMALL.forEach(consumer);
            LANTERNS.forEach(consumer);
            LANTERNS_LARGE.forEach(consumer);

            ALARMS_SMALL.forEach(consumer);
            ALARMS.forEach(consumer);
            ALARMS_LARGE.forEach(consumer);
        }

        public static void register() {
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.CEILING.with(Describer.Attachment.NONE)
            ).accept(CEILINGS);

            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.SLAB.with(Describer.Attachment.NONE)
            ).accept(SLABS);
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.CLEAR.with(Describer.Attachment.NONE)
            ).accept(CLEARS);

            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN.with(Describer.Attachment.SMALL)
            ).accept(LANTERNS_SMALL);
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN.with(Describer.Attachment.NONE)
            ).accept(LANTERNS);
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN.with(Describer.Attachment.LARGE)
            ).accept(LANTERNS_LARGE);

            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM.with(Describer.Attachment.SMALL)
            ).accept(ALARMS_SMALL);
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM.with(Describer.Attachment.NONE)
            ).accept(ALARMS);
            registerColorVariants(
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM.with(Describer.Attachment.LARGE)
            ).accept(ALARMS_LARGE);
        }

        public static <B extends Block> Consumer<HashMap<B, Item>> registerColorVariants(
                Supplier<B> blockSupplier,
                Describer.Wrapper describerWrapper
        ) {
            return hashMap -> {
                hashMap.clear();

                for (DyeColor dyeColor : DyeColor.values()) {
                    Identifier id = LET.id(describerWrapper.getId(dyeColor));
                    describerWrapper.addToDefaultNames(dyeColor);

                    B block = blockSupplier.get();
                    Item item = new ColoredBlockItem(block, new Item.Settings(), dyeColor);

                    // Store registered contents
                    hashMap.put(registerBlock(id, block), registerItem(id, item));

                    // Register tint for blocks & items
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> dyeColor.getSignColor(), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> dyeColor.getSignColor(), item);
                }
            };
        }
    }

    public static class Items {
        public static final Item BULB = registerItem(
                band.kessokuteatime.lightemittingtriode.LET.id("bulb"),
                new Item(new Item.Settings())
        );

        public static final Item LED = registerItem(
                band.kessokuteatime.lightemittingtriode.LET.id("led"),
                new Item(new Item.Settings())
        );

        public static final Item LET = registerItem(
                band.kessokuteatime.lightemittingtriode.LET.id("let"),
                new Item(new Item.Settings())
        );

        public static final Item SHADE = registerItem(
                band.kessokuteatime.lightemittingtriode.LET.id("shade"),
                new Item(new Item.Settings())
        );

        public static void register() {

        }
    }

    public static void register() {
        Blocks.register();
        Items.register();
        ItemGroups.establish();
    }
}
