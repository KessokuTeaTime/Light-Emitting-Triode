package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.base.tag.Dimmable;
import band.kessokuteatime.lightemittingtriode.content.block.base.tag.Dyable;
import band.kessokuteatime.lightemittingtriode.content.item.ShadeItem;
import band.kessokuteatime.lightemittingtriode.content.variant.Variant;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

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
                LightEmittingTriode.id("general"),
                FabricItemGroup.builder()
                        .icon(Items.LET::getDefaultStack)
                        .displayName(LightEmittingTriode.translatable("itemGroup", "general"))
                        .build()
        );

        public static void establish() {
            net.minecraft.registry.Registries.ITEM_GROUP.getKey(GENERAL).ifPresent(key ->
                    ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
                        fill(entries, Items.SHADE, Items.BULB, Items.LED, Items.LET, Items.TUBE);

                        // Guard the items' order
                        Arrays.stream(Blocks.Type.values())
                                .map(Blocks.Type::wrappers)
                                .flatMap(ArrayList::stream)
                                .map(Wrapper::blockItem)
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

            final ArrayList<Wrapper> wrappers;
            final Wrapper.Basis basis;

            Type(Wrapper.Basis basis) {
                this.wrappers = new ArrayList<>();
                this.basis = basis;
            }

            public ArrayList<Wrapper> wrappers() {
                return wrappers;
            }

            public Wrapper.Basis basis() {
                return basis;
            }
        }

        public static void register() {
            registerColorVariantsForTypes(Type.values());
        }

        public static void registerColorVariantsForTypes(Type... types) {
            for (Type type : types) {
                type.wrappers().clear();

                for (DyeColor dyeColor : DyeColor.values()) {
                    Wrapper wrapper = type.basis().with(dyeColor);
                    Identifier id = wrapper.id();

                    Block block = registerBlock(id, wrapper.createBlock());
                    Item item = registerItem(id, wrapper.createBlockItem());

                    // Store the wrapper
                    type.wrappers().add(wrapper);

                    // Register tints
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> wrapper.colorOverlay(
                            ((AbstractLampBlock) state.getBlock()).isLit(state), tintIndex
                    ), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> wrapper.colorOverlay(false, 2), item);

                    // Make translucent
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                }
            }
        }
    }

    public static class Items {
        public static final Item BULB = registerItem(
                LightEmittingTriode.id("bulb"),
                new Item(new Item.Settings())
        );

        public static final Item LED = registerItem(
                LightEmittingTriode.id("led"),
                new Item(new Item.Settings())
        );

        public static final Item LET = registerItem(
                LightEmittingTriode.id("let"),
                new Item(new Item.Settings())
        );

        public static final Item SHADE = registerItem(
                LightEmittingTriode.id("shade"),
                new ShadeItem(new Item.Settings())
        );

        public static final Item TUBE = registerItem(
                LightEmittingTriode.id("tube"),
                new Item(new Item.Settings())
        );

        public static void register() {
        }
    }

    public enum BlockTag {
        DIODES(TagKey.of(RegistryKeys.BLOCK, LightEmittingTriode.id("diodes")), null),
        TRIODES(TagKey.of(RegistryKeys.BLOCK, LightEmittingTriode.id("triodes")), null),
        DIMMABLES(TagKey.of(RegistryKeys.BLOCK, LightEmittingTriode.id("dimmable")), Dimmable.class),
        DYABLES(TagKey.of(RegistryKeys.BLOCK, LightEmittingTriode.id("dyable")), Dyable.class);

        final TagKey<Block> tag;
        @Nullable final Class<?> requiredInterface;

        BlockTag(net.minecraft.registry.tag.TagKey<Block> tag, @Nullable Class<?> requiredInterface) {
            this.tag = tag;
            this.requiredInterface = requiredInterface;
        }

        public TagKey<Block> getTag() {
            return tag;
        }

        public boolean contains(Block block) {
            return requiredInterface == null || requiredInterface.isAssignableFrom(block.getClass());
        }
    }

    public static void register() {
        Blocks.register();
        Items.register();
        ItemGroups.establish();
    }
}
