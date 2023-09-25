package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.VoxelShaper;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.FacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.decorational.SlabFacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.ButtonLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.DetectorLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.functional.SwitchLampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import band.kessokuteatime.lightemittingtriode.content.item.InventoryColoredBlockItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public enum Variant {
    CLEAR("clear", size -> 15,
            size -> VoxelShapes.fullCube(),

            LampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.AMETHYST_BLOCK),
                    () -> List.of(ModRegistries.BlockTag.TRIODES, ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(ModRegistries.Items.LET, 4)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    null
            )
    ),
    SLAB("slab", size -> 13,
            size -> VoxelShaper.fromBottomCenter(16, 8),
            SlabFacingLampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.AMETHYST_BLOCK),
                    () -> List.of(ModRegistries.BlockTag.TRIODES, ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(ModRegistries.Items.LET, 2)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    wrapper -> SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(Variant.CLEAR.with(Size.NORMAL), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
            )
    ),
    CEILING("ceiling", size -> 10,
            size -> VoxelShaper.fromBottomCenter(16, 1),
            FacingLampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    () -> List.of(ModRegistries.BlockTag.TRIODES, ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(ModRegistries.Items.LET)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    wrapper -> SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(Variant.SLAB.with(Size.NORMAL), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
            )
    ),
    LANTERN("lantern", size -> 8 + size * 2,
            size -> VoxelShaper.fromBottomCenter(4 + 2 * size, 6 + size),
            FacingLampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.LANTERN)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    () -> List.of(ModRegistries.BlockTag.DIODES, ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(Items.IRON_NUGGET)
                            .input(ModRegistries.Items.LED, 1 + wrapper.basis().size().getSize())
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(Items.IRON_NUGGET),
                                    FabricRecipeProvider.conditionsFromItem(Items.IRON_NUGGET))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    wrapper -> wrapper.basis().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LED),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(Variant.valueOf("LANTERN").with(wrapper.basis().size().larger()), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
            )
    ),
    ALARM("alarm", size -> 5 + size * 3,
            size -> VoxelShaper.fromBottomCenter(10 + 2 * size, 1),
            FacingLampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.CHAIN)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    () -> List.of(ModRegistries.BlockTag.DIODES, ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(Items.GOLD_NUGGET)
                            .input(ModRegistries.Items.LED, 1 + wrapper.basis().size().getSize())
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(Items.GOLD_NUGGET),
                                    FabricRecipeProvider.conditionsFromItem(Items.GOLD_NUGGET))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    wrapper -> wrapper.basis().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(ModRegistries.Items.LED),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(Variant.valueOf("ALARM").with(wrapper.basis().size().larger()), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
            )
    ),
    SWITCH("switch", size -> 1,
            size -> VoxelShaper.fromBottomCenter(8, 2),
            SwitchLampBlock::new,
            InventoryColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.EMITS_LIGHT
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    List::of,

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(Items.QUARTZ)
                            .input(ModRegistries.Items.TUBE)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(Items.QUARTZ),
                                    FabricRecipeProvider.conditionsFromItem(Items.QUARTZ))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    null
            )
    ),
    BUTTON("button", size -> 1,
            size -> Block.createCuboidShape(6, 0, 5, 10, 2, 11),
            wrapper -> new ButtonLampBlock(wrapper, 22),
            InventoryColoredBlockItem::new,

            List.of(
                    ModTooltip.EMPTY,
                    ModTooltip.EMITS_LIGHT
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.CHERRY_WOOD)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    List::of,

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(ModRegistries.Items.TUBE)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    wrapper -> SmithingTransformRecipeJsonBuilder.create(
                                    Ingredient.empty(),
                                    Ingredient.ofItems(wrapper.blockItem()), Ingredient.ofItems(Items.QUARTZ),
                                    RecipeCategory.BUILDING_BLOCKS,
                                    Wrapper.blockItem(Variant.SWITCH.with(Size.NORMAL), wrapper.dyeColor())
                            )
                            .criterion(FabricRecipeProvider.hasItem(wrapper.blockItem()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.blockItem()))
                            .criterion(FabricRecipeProvider.hasItem(Items.QUARTZ),
                                    FabricRecipeProvider.conditionsFromItem(Items.QUARTZ))
            )
    ),
    DETECTOR("detector", size -> 5,
            size -> VoxelShaper.fromBottomCenter(16, 1),
            DetectorLampBlock::new,
            ColoredBlockItem::new,

            List.of(
                    ModTooltip.DETECTOR,
                    ModTooltip.EMPTY,
                    ModTooltip.EMITS_LIGHT, ModTooltip.DYABLE, ModTooltip.DIMMABLE
            ),

            new DataProvider(
                    () -> AbstractBlock.Settings.copy(Blocks.GLASS)
                            .sounds(BlockSoundGroup.CHERRY_WOOD)
                            .pistonBehavior(PistonBehavior.DESTROY),
                    () -> List.of(ModRegistries.BlockTag.DIMMABLES, ModRegistries.BlockTag.DYABLES),

                    wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                            .input(ModRegistries.Items.TUBE, 2)
                            .input(ModRegistries.Items.SHADE)
                            .input(Items.GOLD_NUGGET)
                            .input(wrapper.dye())
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.TUBE),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.TUBE))
                            .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.SHADE),
                                    FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.SHADE))
                            .criterion(FabricRecipeProvider.hasItem(Items.GOLD_NUGGET),
                                    FabricRecipeProvider.conditionsFromItem(Items.GOLD_NUGGET))
                            .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                                    FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

                    null
            )
    );

    public enum Size {
        NORMAL(null, 1),
        SMALL("small", 0),
        LARGE("large", 2);

        final @Nullable String id;
        final int size;

        Size(@Nullable String id, int size) {
            this.id = id;
            this.size = size;
        }

        public Optional<String> getId() {
            return id != null ? Optional.of(id) : Optional.empty();
        }

        public int getSize() {
            return size;
        }

        public Size get(int size) {
            return Arrays.stream(values()).filter(s -> s.getSize() == size).findFirst().orElse(NORMAL);
        }

        public Size larger() {
            return get(MathHelper.clamp(getSize() + 1, 0, 2));
        }

        public Size smaller() {
            return get(MathHelper.clamp(getSize() - 1, 0, 2));
        }
    }

    final String id;
    final IntFunction<Integer> luminance;
    final Function<Integer, VoxelShape> voxelShapeProvider;
    final Function<Wrapper, Block> blockProvider;
    final Function<Wrapper, BlockItem> blockItemProvider;
    final List<ModTooltip> tooltips;
    final DataProvider dataProvider;

    Variant(
            String id, IntFunction<Integer> luminance,
            Function<Integer, VoxelShape> voxelShapeProvider,
            Function<Wrapper, Block> blockProvider,
            Function<Wrapper, BlockItem> blockItemProvider,
            List<ModTooltip> tooltips,
            DataProvider dataProvider
    ) {
        this.id = id;
        this.luminance = luminance;
        this.voxelShapeProvider = voxelShapeProvider;
        this.blockProvider = blockProvider;
        this.blockItemProvider = blockItemProvider;
        this.tooltips = tooltips;
        this.dataProvider = dataProvider;
    }

    public String getId() {
        return id;
    }

    public List<ModTooltip> getTooltips() {
        return tooltips;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public int luminance(int size) {
        return luminance.apply(size);
    }

    public VoxelShape voxelShape(int size) {
        return voxelShapeProvider.apply(size);
    }

    public Block createBlock(Wrapper wrapper) {
        return blockProvider.apply(wrapper);
    }

    public BlockItem createBlockItem(Wrapper wrapper) {
        return blockItemProvider.apply(wrapper);
    }

    public Wrapper.Basis with(Size size) {
        return new Wrapper.Basis(this, size);
    }
}
