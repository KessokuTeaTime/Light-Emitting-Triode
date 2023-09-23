package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.item.ShadeItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;

public class ModRegistries {
    private static <B extends Block> B registerBlock(Identifier id, B block) {
        return Registry.register(net.minecraft.registry.Registries.BLOCK, id, block);
    }

    private static <I extends Item> I registerItem(Identifier id, I item) {
        return Registry.register(net.minecraft.registry.Registries.ITEM, id, item);
    }

    public static class ItemGroups {
        public static final ItemGroup GENERAL = Registry.register(
                net.minecraft.registry.Registries.ITEM_GROUP,
                LET.id("general"),
                FabricItemGroup.builder()
                        .icon(Items.LET::getDefaultStack)
                        .displayName(LET.translatable("itemGroup", "general"))
                        .build()
        );

        public static void establish() {
            net.minecraft.registry.Registries.ITEM_GROUP.getKey(GENERAL).ifPresent(key ->
                    ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
                        fill(entries, Items.SHADE, Items.BULB, Items.LED, Items.LET, Items.TUBE);

                        Arrays.stream(Blocks.Type.values())
                                .map(Blocks.Type::blockItemMap)
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
            CLEAR(Variant.CLEAR.with(Variant.Size.NORMAL)),

            SLAB(Variant.SLAB.with(Variant.Size.NORMAL)),
            CEILING(Variant.CEILING.with(Variant.Size.NORMAL)),

            LANTERN_SMALL(Variant.LANTERN.with(Variant.Size.SMALL)),
            LANTERN(Variant.LANTERN.with(Variant.Size.NORMAL)),
            LANTERN_LARGE(Variant.LANTERN.with(Variant.Size.LARGE)),

            ALARM_SMALL(Variant.ALARM.with(Variant.Size.SMALL)),
            ALARM(Variant.ALARM.with(Variant.Size.NORMAL)),
            ALARM_LARGE(Variant.ALARM.with(Variant.Size.LARGE)),

            SWITCH(Variant.SWITCH.with(Variant.Size.NORMAL)),
            BUTTON(Variant.BUTTON.with(Variant.Size.NORMAL)),

            DETECTOR(Variant.DETECTOR.with(Variant.Size.NORMAL));

            final HashMap<Block, BlockItem> blockItemMap;
            final Variant.Basis basis;

            Type(Variant.Basis basis) {
                this.blockItemMap = new HashMap<>();
                this.basis = basis;
            }

            public HashMap<Block, BlockItem> blockItemMap() {
                return blockItemMap;
            }

            public Variant.Basis basis() {
                return basis;
            }
        }

        public static void register() {
            registerColorVariantsForTypes(
                    Type.CLEAR,
                    Type.SLAB,

                    Type.CEILING,

                    Type.LANTERN_SMALL,
                    Type.LANTERN,
                    Type.LANTERN_LARGE,

                    Type.ALARM_SMALL,
                    Type.ALARM,
                    Type.ALARM_LARGE,

                    Type.SWITCH,
                    Type.BUTTON,

                    Type.DETECTOR
            );
        }

        public static void registerColorVariantsForTypes(
                Type... types
        ) {
            for (Type type : types) {
                type.blockItemMap().clear();

                for (DyeColor dyeColor : DyeColor.values()) {
                    Variant.Wrapper wrapper = type.basis().with(dyeColor);
                    Identifier id = wrapper.id();

                    Block block = wrapper.createBlock();
                    Item item = wrapper.createBlockItem(block);

                    // Store registered contents
                    type.blockItemMap().put(registerBlock(id, block), (BlockItem) registerItem(id, item));

                    // Register tints
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> wrapper.colorOverlay(state.get(Properties.LIT), tintIndex), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> wrapper.colorOverlay(false, 1), item);

                    // Make translucent
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                }
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
                new ShadeItem(new Item.Settings())
        );

        public static final Item TUBE = registerItem(
                band.kessokuteatime.lightemittingtriode.LET.id("tube"),
                new Item(new Item.Settings())
        );

        public static void register() {

        }
    }

    public static class BlockTags {
        public static final TagKey<Block> DIODES = TagKey.of(RegistryKeys.BLOCK, LET.id("diodes"));
        public static final TagKey<Block> TRIODES = TagKey.of(RegistryKeys.BLOCK, LET.id("triodes"));
        public static final TagKey<Block> DIMMABLES = TagKey.of(RegistryKeys.BLOCK, LET.id("dimmables"));
    }

    public static void register() {
        Blocks.register();
        Items.register();
        ItemGroups.establish();
    }
}
