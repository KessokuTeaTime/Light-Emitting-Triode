package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.block.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

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

            final HashMap<Block, Item> blockItemMap;
            final Variant.IdPack idPack;

            Type(Variant.IdPack idPack) {
                this.blockItemMap = new HashMap<>();
                this.idPack = idPack;
            }

            public HashMap<Block, Item> getBlockItemMap() {
                return blockItemMap;
            }

            public Variant.IdPack getIdPack() {
                return idPack;
            }
        }

        public static void register() {
            registerColorVariants(
                    dyeColor -> Variant.CEILING.with(Variant.Size.NORMAL, dyeColor)
            ).accept(Type.CEILING.getBlockItemMap());

            registerColorVariants(
                    dyeColor -> Variant.SLAB.with(Variant.Size.NORMAL, dyeColor)
            ).accept(Type.SLAB.getBlockItemMap());
            registerColorVariants(
                    dyeColor -> Variant.CLEAR.with(Variant.Size.NORMAL, dyeColor)
            ).accept(Type.CLEAR.getBlockItemMap());

            registerColorVariants(
                    dyeColor -> Variant.LANTERN.with(Variant.Size.SMALL, dyeColor)
            ).accept(Type.LANTERN_SMALL.getBlockItemMap());
            registerColorVariants(
                    dyeColor -> Variant.LANTERN.with(Variant.Size.NORMAL, dyeColor)
            ).accept(Type.LANTERN.getBlockItemMap());
            registerColorVariants(
                    dyeColor -> Variant.LANTERN.with(Variant.Size.LARGE, dyeColor)
            ).accept(Type.LANTERN_LARGE.getBlockItemMap());

            registerColorVariants(
                    dyeColor -> Variant.ALARM.with(Variant.Size.SMALL, dyeColor)
            ).accept(Type.ALARM_SMALL.getBlockItemMap());
            registerColorVariants(
                    dyeColor -> Variant.ALARM.with(Variant.Size.NORMAL, dyeColor)
            ).accept(Type.ALARM.getBlockItemMap());
            registerColorVariants(
                    dyeColor -> Variant.ALARM.with(Variant.Size.LARGE, dyeColor)
            ).accept(Type.ALARM_LARGE.getBlockItemMap());
        }

        public static Consumer<HashMap<Block, Item>> registerColorVariants(
                Function<DyeColor, Variant.Wrapper> wrapperSupplier
        ) {
            return hashMap -> {
                hashMap.clear();

                for (DyeColor dyeColor : DyeColor.values()) {
                    Variant.Wrapper wrapper = wrapperSupplier.apply(dyeColor);
                    Identifier id = wrapper.id();

                    Block block = wrapper.block();
                    Item item = new ColoredBlockItem(block, new Item.Settings(), dyeColor);

                    // Store registered contents
                    hashMap.put(registerBlock(id, block), registerItem(id, item));

                    // Register tints
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> wrapper.colorOverlay(state.get(Properties.LIT), tintIndex), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> wrapper.colorOverlay(false, 1), item);

                    // Make translucent
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
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
