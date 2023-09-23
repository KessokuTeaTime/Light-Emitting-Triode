package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.VoxelShapingTool;
import band.kessokuteatime.lightemittingtriode.content.block.FacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.SlabFacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum Variant {
    CLEAR("clear", 7,
            size -> VoxelShapes.fullCube(),
            LampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET, 4)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            null
    ),
    SLAB("slab", 10,
            size -> VoxelShapingTool.fromBottomCenter(16, 8),
            SlabFacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET, 2)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.block().asItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                            RecipeCategory.BUILDING_BLOCKS, wrapper.blockItem(Variant.CLEAR.with(Size.NORMAL))
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.block().asItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.block().asItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    ),
    CEILING("ceiling", 10,
            size -> VoxelShapingTool.fromBottomCenter(16, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(ModRegistries.Items.LET)
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> SmithingTransformRecipeJsonBuilder.create(
                    Ingredient.empty(),
                    Ingredient.ofItems(wrapper.block().asItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                    RecipeCategory.BUILDING_BLOCKS, wrapper.blockItem(Variant.SLAB.with(Size.NORMAL))
            )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.block().asItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.block().asItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    ),
    LANTERN("lantern", 5,
            size -> VoxelShapingTool.fromBottomCenter(4 + 2 * size, 6 + size),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(Items.IRON_NUGGET)
                    .input(ModRegistries.Items.LED, 1 + wrapper.fixed().size().getSize())
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(Items.IRON_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(Items.IRON_NUGGET))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> wrapper.fixed().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.block().asItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                            RecipeCategory.BUILDING_BLOCKS, wrapper.blockItem(Variant.valueOf("LANTERN").with(wrapper.fixed().size().larger()))
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.block().asItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.block().asItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    ),
    ALARM("alarm", 3,
            size -> VoxelShapingTool.fromBottomCenter(10 + 2 * size, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor),

            wrapper -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, wrapper.block())
                    .input(Items.GOLD_NUGGET)
                    .input(ModRegistries.Items.LED, 1 + wrapper.fixed().size().getSize())
                    .input(wrapper.dye())
                    .criterion(FabricRecipeProvider.hasItem(Items.GOLD_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(Items.GOLD_NUGGET))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LED),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LED))
                    .criterion(FabricRecipeProvider.hasItem(wrapper.dye()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.dye())),

            wrapper -> wrapper.fixed().size().getSize() >= 2 ? null : SmithingTransformRecipeJsonBuilder.create(
                            Ingredient.empty(),
                            Ingredient.ofItems(wrapper.block().asItem()), Ingredient.ofItems(ModRegistries.Items.LET),
                            RecipeCategory.BUILDING_BLOCKS, wrapper.blockItem(Variant.valueOf("ALARM").with(wrapper.fixed().size().larger()))
                    )
                    .criterion(FabricRecipeProvider.hasItem(wrapper.block().asItem()),
                            FabricRecipeProvider.conditionsFromItem(wrapper.block().asItem()))
                    .criterion(FabricRecipeProvider.hasItem(ModRegistries.Items.LET),
                            FabricRecipeProvider.conditionsFromItem(ModRegistries.Items.LET))
    );

    public static Vec3d placeParticle(BlockPos blockPos, VoxelShape voxelShape, Random random, double factor) {
        Vec3d offset = new Vec3d(
                (random.nextDouble() * 2 - 1) * factor,
                (random.nextDouble() * 2 - 1) * factor,
                (random.nextDouble() * 2 - 1) * factor
        );

        Vec3d localSize = new Vec3d(
                voxelShape.getMax(Direction.Axis.X) - voxelShape.getMin(Direction.Axis.X),
                voxelShape.getMax(Direction.Axis.Y) - voxelShape.getMin(Direction.Axis.Y),
                voxelShape.getMax(Direction.Axis.Z) - voxelShape.getMin(Direction.Axis.Z)
        );

        Vec3d localCenter = new Vec3d(
                (voxelShape.getMin(Direction.Axis.X) + voxelShape.getMax(Direction.Axis.X)) / 2,
                (voxelShape.getMin(Direction.Axis.Y) + voxelShape.getMax(Direction.Axis.Y)) / 2,
                (voxelShape.getMin(Direction.Axis.Z) + voxelShape.getMax(Direction.Axis.Z)) / 2
        );

        Vec3d pos = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return pos.add(localCenter.add(
                localSize.getX() * offset.getX(),
                localSize.getY() * offset.getY(),
                localSize.getZ() * offset.getZ()
        ));
    }

    public record Fixed(Variant variant, Size size) {
        private String genericIdString() {
            return variant().getId() + size().getId().map(p -> "_" + p).orElse("");
        }

        public Identifier genericId() {
            return LET.id("block", genericIdString());
        }
    }

    public record Wrapper(Fixed fixed, DyeColor dyeColor) {
        public String idString(Fixed fixed) {
            return fixed.variant().getId()
                    + fixed.size().getId().map(p -> "_" + p).orElse("")
                    + "_" + dyeColor().getName();
        }

        public String idString() {
            return idString(fixed());
        }

        public Identifier id() {
            return LET.id(idString());
        }

        public Identifier categorizedId(String category) {
            return LET.id(category, idString());
        }

        public int color() {
            return LET.getColorFromDye(dyeColor());
        }

        public int colorOverlay(boolean lit, int tintIndex) {
            return switch (tintIndex) {
                case 0 -> LET.mapColorRange(color(), lit ? 0xE4 : 0, lit ? 0 : 0xD2);
                case 1 -> LET.mapColorRange(color(), lit ? 0x80 : 0x10, lit ? 0x10 : 0x80);
                default -> color();
            };
        }

        public Vec3d placeParticle(BlockPos blockPos, Random random, double factor) {
            return Variant.placeParticle(blockPos, voxelShape(), random, factor);
        }

        public int luminance() {
            return fixed().variant().getLuminance(fixed().size().getSize());
        }

        public VoxelShape voxelShape() {
            return fixed().variant().getVoxelShape(fixed().size().getSize());
        }

        public Block createBlock() {
            return fixed().variant().getBlock(this);
        }

        public BlockItem createBlockItem(Block block) {
            return fixed().variant().getBlockItem(block, dyeColor());
        }

        public Block block(Fixed fixed) {
            return Registries.BLOCK.get(LET.id(idString(fixed)));
        }

        public Block block() {
            return block(fixed());
        }

        public BlockItem blockItem(Fixed fixed) {
            return (BlockItem) Registries.ITEM.get(LET.id(idString(fixed)));
        }

        public BlockItem blockItem() {
            return blockItem(fixed());
        }

        public Item dye() {
            return Registries.ITEM.get(new Identifier(dyeColor().getName() + "_dye"));
        }

        public Optional<CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder() {
            return Optional.ofNullable(fixed().variant().craftingRecipeJsonBuilder(this));
        }

        public Optional<SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder() {
            return Optional.ofNullable(fixed().variant().upgradingRecipeJsonBuilder(this));
        }
    }

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
    final int luminance;
    final Function<Integer, VoxelShape> voxelShapeProvider;
    final Function<Wrapper, Block> blockProvider;
    final BiFunction<Block, DyeColor, BlockItem> blockItemProvider;
    @Nullable final Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder;
    @Nullable final Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder;

    Variant(
            String id, int luminance,
            Function<Integer, VoxelShape> voxelShapeProvider,
            Function<Wrapper, Block> blockProvider,
            BiFunction<Block, DyeColor, BlockItem> blockItemProvider,
            @Nullable Function<Wrapper, CraftingRecipeJsonBuilder> craftingRecipeJsonBuilder,
            @Nullable Function<Wrapper, SmithingTransformRecipeJsonBuilder> upgradingRecipeJsonBuilder
    ) {
        this.id = id;
        this.luminance = luminance;
        this.voxelShapeProvider = voxelShapeProvider;
        this.blockProvider = blockProvider;
        this.blockItemProvider = blockItemProvider;
        this.craftingRecipeJsonBuilder = craftingRecipeJsonBuilder;
        this.upgradingRecipeJsonBuilder = upgradingRecipeJsonBuilder;
    }

    public Fixed with(Size size) {
        return new Fixed(this, size);
    }

    public Wrapper with(Size size, DyeColor dyeColor) {
        return new Wrapper(new Fixed(this, size), dyeColor);
    }

    public String getId() {
        return id;
    }

    public int getLuminance(int size) {
        return luminance + 2 * size + 1;
    }

    public VoxelShape getVoxelShape(int size) {
        return voxelShapeProvider.apply(size);
    }

    public Block getBlock(Wrapper wrapper) {
        return blockProvider.apply(wrapper);
    }

    public BlockItem getBlockItem(Block block, DyeColor dyeColor) {
        return blockItemProvider.apply(block, dyeColor);
    }

    public @Nullable CraftingRecipeJsonBuilder craftingRecipeJsonBuilder(Wrapper wrapper) {
        return craftingRecipeJsonBuilder == null ? null : craftingRecipeJsonBuilder.apply(wrapper);
    }

    public @Nullable SmithingTransformRecipeJsonBuilder upgradingRecipeJsonBuilder(Wrapper wrapper) {
        return upgradingRecipeJsonBuilder == null ? null : upgradingRecipeJsonBuilder.apply(wrapper);
    }
}
