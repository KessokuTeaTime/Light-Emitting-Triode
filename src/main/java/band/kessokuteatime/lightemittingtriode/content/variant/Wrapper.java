package band.kessokuteatime.lightemittingtriode.content.variant;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public record Wrapper(Basis basis, DyeColor dyeColor, UnaryOperator<AbstractBlock.Settings> settingsWrapper) {
    public Wrapper(Basis basis, DyeColor dyeColor) {
        this(basis, dyeColor, settings -> settings);
    }

    public record Basis(Variant variant, Variant.Size size) {
        public Basis variant(UnaryOperator<Variant> variantOperator) {
            return new Basis(variantOperator.apply(variant()), size());
        }

        public String genericIdString(String... postfixes) {
            return variant().getId()
                    + size().getId().map(p -> "_" + p).orElse("")
                    + Arrays.stream(postfixes)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .map(s -> "_" + s)
                    .collect(Collectors.joining());
        }

        public Identifier genericId(String... postfixes) {
            return LightEmittingTriode.id("block", genericIdString(postfixes));
        }

        public Wrapper with(DyeColor dyeColor) {
            return new Wrapper(this, dyeColor);
        }
    }

    public ArrayList<Wrapper> wrappersOfOtherColors() {
        return Arrays.stream(DyeColor.values())
                .filter(d -> d != dyeColor())
                .map(this::dyeColor)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static String idString(Basis basis, DyeColor dyeColor) {
        return basis.variant().getId()
                + basis.size().getId().map(p -> "_" + p).orElse("")
                + "_" + dyeColor.getName();
    }

    public static Block block(Basis basis, DyeColor dyeColor) {
        return Registries.BLOCK.get(LightEmittingTriode.id(idString(basis, dyeColor)));
    }

    public static BlockItem blockItem(Basis basis, DyeColor dyeColor) {
        return (BlockItem) Registries.ITEM.get(LightEmittingTriode.id(idString(basis, dyeColor)));
    }

    public Wrapper dyeColor(DyeColor dyeColor) {
        return new Wrapper(basis(), dyeColor, settingsWrapper());
    }

    public Wrapper wrapSettings(UnaryOperator<AbstractBlock.Settings> futureSettingsWrapper) {
        return new Wrapper(
                basis(), dyeColor(),
                settings -> settingsWrapper().apply(futureSettingsWrapper.apply(settings))
        );
    }

    public String idString() {
        return idString(basis(), dyeColor());
    }

    public Identifier id() {
        return LightEmittingTriode.id(idString());
    }

    public Identifier categorizedId(String... categories) {
        ArrayList<String> paths = new ArrayList<>(List.of(categories));
        paths.add(idString());

        return LightEmittingTriode.id(paths.toArray(new String[]{}));
    }

    public AbstractBlock.Settings buildSettings() {
        return settingsWrapper().apply(basis().variant().getDataProvider().settingsSupplier().get());
    }

    public List<ModRegistries.BlockTag> tags() {
        return basis().variant().getDataProvider().tagSupplier().get();
    }

    public boolean isIn(ModRegistries.BlockTag blockTag) {
        return tags().contains(blockTag) && blockTag.contains(block());
    }

    public int color() {
        return LightEmittingTriode.getColorFromDyeColor(dyeColor());
    }

    public int colorOverlay(boolean lit, int tintIndex) {
        return switch (tintIndex) {
            default -> color();
            // Outer
            case 0 -> LightEmittingTriode.mapColorRange(color(), lit ? 0xAF : 0x00, lit ? 0x00 : 0xAB);
            // Inner
            case 1 -> LightEmittingTriode.mapColorRange(color(), lit ? 0x10 : 0x00, lit ? 0x00 : 0x10);
            // Item
            case 2 -> LightEmittingTriode.mapColorRange(color(), 0x20, 0x40);
        };
    }

    public int luminance() {
        return basis().variant().luminance(basis().size().getSize());
    }

    public VoxelShape voxelShape() {
        return basis().variant().voxelShape(basis().size().getSize());
    }

    public Block createBlock() {
        return basis().variant().createBlock(this);
    }

    public BlockItem createBlockItem() {
        return basis().variant().createBlockItem(this);
    }

    public Block block() {
        return block(basis(), dyeColor());
    }

    public BlockItem blockItem() {
        return blockItem(basis(), dyeColor());
    }

    public Item dye() {
        return Registries.ITEM.get(new Identifier(dyeColor().getName() + "_dye"));
    }

    public Vec3d generateSurfaceParticlePos(
            BlockState state, BlockView world, BlockPos pos,
            ShapeContext context, Random random, double factor
    ) {
        return LightEmittingTriode.generateSurfaceParticlePos(
                pos, block().getOutlineShape(state, world, pos, context),
                random, factor
        );
    }

    public Consumer<Consumer<RecipeJsonProvider>> useCraftingRecipeJsonBuilder() {
        return exporter -> basis().variant().getDataProvider().craftingRecipeJsonBuilder()
                .map(builder -> builder.apply(this))
                .ifPresent(builder -> builder.offerTo(exporter, categorizedId("crafting")));
    }

    public Consumer<Consumer<RecipeJsonProvider>> useUpgradingRecipeJsonBuilder() {
        return exporter -> basis().variant().getDataProvider().upgradingRecipeJsonBuilder()
                .map(builder -> builder.apply(this))
                .ifPresent(builder -> builder.offerTo(exporter, categorizedId("upgrading")));
    }

    public Consumer<Consumer<RecipeJsonProvider>> useRecoloringRecipeJsonBuilders() {
        return exporter -> basis().variant().getDataProvider().recoloringRecipeJsonBuilder()
                .ifPresent(builder ->
                        wrappersOfOtherColors().stream()
                                .map(wrapper -> new AbstractMap.SimpleEntry<>(
                                        wrapper,
                                        builder.apply(Ingredient.ofItems(blockItem()), wrapper)
                                ))
                                .forEach(entry -> entry.getValue().offerTo(
                                        exporter,
                                        categorizedId("recoloring", "to_" + entry.getKey().dyeColor().getName())
                                ))
                );
    }
}
