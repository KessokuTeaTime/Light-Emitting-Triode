package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.block.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
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

                        Arrays.stream(Blocks.Type.values())
                                .map(Blocks.Type::getBlockItemMap)
                                .flatMap(map -> map.values().stream())
                                .forEach(entries::add);
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
        public enum Type {
            CEILING(Variant.CEILING.with(Variant.Size.NORMAL)),

            SLAB(Variant.SLAB.with(Variant.Size.NORMAL)),
            CLEAR(Variant.CLEAR.with(Variant.Size.NORMAL)),

            LANTERN_SMALL(Variant.LANTERN.with(Variant.Size.SMALL)),
            LANTERN(Variant.LANTERN.with(Variant.Size.NORMAL)),
            LANTERN_LARGE(Variant.LANTERN.with(Variant.Size.LARGE)),

            ALARM_SMALL(Variant.ALARM.with(Variant.Size.SMALL)),
            ALARM(Variant.ALARM.with(Variant.Size.NORMAL)),
            ALARM_LARGE(Variant.ALARM.with(Variant.Size.LARGE));

            final HashMap<LampBlock, ColoredBlockItem> blockItemMap;
            final Variant.Wrapper wrapper;

            Type(Variant.Wrapper wrapper) {
                this.blockItemMap = new HashMap<>();
                this.wrapper = wrapper;
            }

            public HashMap<LampBlock, ColoredBlockItem> getBlockItemMap() {
                return blockItemMap;
            }

            public Variant.Wrapper getWrapper() {
                return wrapper;
            }
        }

        public static void register() {
            registerColorVariants(
                    Variant.CEILING.with(Variant.Size.NORMAL)
            ).accept(Type.CEILING.getBlockItemMap());

            registerColorVariants(
                    Variant.SLAB.with(Variant.Size.NORMAL)
            ).accept(Type.SLAB.getBlockItemMap());
            registerColorVariants(
                    Variant.CLEAR.with(Variant.Size.NORMAL)
            ).accept(Type.CLEAR.getBlockItemMap());

            registerColorVariants(
                    Variant.LANTERN.with(Variant.Size.SMALL)
            ).accept(Type.LANTERN_SMALL.getBlockItemMap());
            registerColorVariants(
                    Variant.LANTERN.with(Variant.Size.NORMAL)
            ).accept(Type.LANTERN.getBlockItemMap());
            registerColorVariants(
                    Variant.LANTERN.with(Variant.Size.LARGE)
            ).accept(Type.LANTERN_LARGE.getBlockItemMap());

            registerColorVariants(
                    Variant.ALARM.with(Variant.Size.SMALL)
            ).accept(Type.ALARM_SMALL.getBlockItemMap());
            registerColorVariants(
                    Variant.ALARM.with(Variant.Size.NORMAL)
            ).accept(Type.ALARM.getBlockItemMap());
            registerColorVariants(
                    Variant.ALARM.with(Variant.Size.LARGE)
            ).accept(Type.ALARM_LARGE.getBlockItemMap());
        }

        public static Consumer<HashMap<LampBlock, ColoredBlockItem>> registerColorVariants(
                Variant.Wrapper wrapper
        ) {
            return hashMap -> {
                hashMap.clear();

                for (DyeColor dyeColor : DyeColor.values()) {
                    Identifier id = wrapper.id(dyeColor);

                    LampBlock block = new LampBlock(wrapper);
                    ColoredBlockItem item = new ColoredBlockItem(block, new Item.Settings(), dyeColor);

                    // Store registered contents
                    hashMap.put(registerBlock(id, block), registerItem(id, item));

                    // Register tints
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> dyeColor.getFireworkColor(), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> dyeColor.getFireworkColor(), item);
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
