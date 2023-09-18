package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
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
                    CEILINGS, "ceiling",
                    () -> new Block(FabricBlockSettings.create())
            );

            registerColorVariants(
                    SLABS, "slab",
                    () -> new Block(FabricBlockSettings.create())
            );
            registerColorVariants(
                    CLEARS, "clear",
                    () -> new Block(FabricBlockSettings.create())
            );

            registerColorVariants(
                    LANTERNS_SMALL, "lantern_small",
                    () -> new Block(FabricBlockSettings.create())
            );
            registerColorVariants(
                    LANTERNS, "lantern",
                    () -> new Block(FabricBlockSettings.create())
            );
            registerColorVariants(
                    LANTERNS_LARGE, "lantern_large",
                    () -> new Block(FabricBlockSettings.create())
            );

            registerColorVariants(
                    ALARMS_SMALL, "alarm_small",
                    () -> new Block(FabricBlockSettings.create())
            );
            registerColorVariants(
                    ALARMS, "alarm",
                    () -> new Block(FabricBlockSettings.create())
            );
            registerColorVariants(
                    ALARMS_LARGE, "alarm_large",
                    () -> new Block(FabricBlockSettings.create())
            );
        }

        public static <B extends Block> void registerColorVariants(HashMap<Block, Item> result, String name, Supplier<B> blockSupplier) {
            result.clear();

            for (DyeColor dyeColor : DyeColor.values()) {
                Identifier id = LET.id(name + "_" + dyeColor.getName());
                B block = blockSupplier.get();

                result.put(
                        registerBlock(id, block),
                        registerItem(id, new BlockItem(block, new Item.Settings()))
                );

                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> dyeColor.getSignColor(), block);
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
