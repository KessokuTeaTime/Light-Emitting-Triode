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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LETRegistries {
    private static final HashMap<Item, String> DEFAULT_NAMES = new HashMap<>();

    private static void addDefaultName(Item item, String name) {
        DEFAULT_NAMES.put(item, name);
    }

    public static String getDefaultName(Item item) {
        return DEFAULT_NAMES.getOrDefault(item, "");
    }

    private static Block registerBlock(Identifier id, Block block) {
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static Item registerItem(Identifier id, Item item) {
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

                        Blocks.forEach(hashMap -> hashMap.values().forEach(entries::add));
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

        public static void forEach(Consumer<HashMap<Block, Item>> consumer) {
            consumer.accept(CEILINGS);

            consumer.accept(SLABS);
            consumer.accept(CLEARS);

            consumer.accept(LANTERNS_SMALL);
            consumer.accept(LANTERNS);
            consumer.accept(LANTERNS_LARGE);

            consumer.accept(ALARMS_SMALL);
            consumer.accept(ALARMS);
            consumer.accept(ALARMS_LARGE);
        }

        public static void register() {
            registerColorVariants(
                    CEILINGS,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.CEILING, Describer.Attachment.NONE
            );

            registerColorVariants(
                    SLABS,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.SLAB, Describer.Attachment.NONE
            );
            registerColorVariants(
                    CLEARS,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.CLEAR, Describer.Attachment.NONE
            );

            registerColorVariants(
                    LANTERNS_SMALL,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN, Describer.Attachment.SMALL
            );
            registerColorVariants(
                    LANTERNS,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN, Describer.Attachment.NONE
            );
            registerColorVariants(
                    LANTERNS_LARGE,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.LANTERN, Describer.Attachment.LARGE
            );

            registerColorVariants(
                    ALARMS_SMALL,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM, Describer.Attachment.SMALL
            );
            registerColorVariants(
                    ALARMS,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM, Describer.Attachment.NONE
            );
            registerColorVariants(
                    ALARMS_LARGE,
                    () -> new Block(FabricBlockSettings.create()),
                    Describer.ALARM, Describer.Attachment.LARGE
            );
        }

        public static <B extends Block> void registerColorVariants(
                HashMap<Block, Item> result,
                Supplier<B> blockSupplier,
                Describer describer,
                Describer.Attachment prefix
        ) {
            result.clear();

            for (DyeColor dyeColor : DyeColor.values()) {
                Identifier id = LET.id(describer.getId(dyeColor, prefix));
                B block = blockSupplier.get();
                BlockItem item = new ColoredBlockItem(block, new Item.Settings(), dyeColor);

                result.put(registerBlock(id, block), registerItem(id, item));

                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> dyeColor.getSignColor(), block);
                addDefaultName(item, describer.getName(dyeColor, prefix));
            }
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
