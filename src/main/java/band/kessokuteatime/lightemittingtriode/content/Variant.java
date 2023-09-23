package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.VoxelShapingTool;
import band.kessokuteatime.lightemittingtriode.content.block.FacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.LampBlock;
import band.kessokuteatime.lightemittingtriode.content.block.SlabFacingLampBlock;
import band.kessokuteatime.lightemittingtriode.content.item.ColoredBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum Variant {
    CEILING("ceiling", 10,
            size -> VoxelShapingTool.fromBottomCenter(16, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor)
    ),
    SLAB("slab", 10,
            size -> VoxelShapingTool.fromBottomCenter(16, 8),
            SlabFacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor)
    ),
    CLEAR("clear", 7,
            size -> VoxelShapes.fullCube(),
            LampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor)
    ),
    LANTERN("lantern", 5,
            size -> VoxelShapingTool.fromBottomCenter(4 + 2 * size, 6 + size),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor)
    ),
    ALARM("alarm", 3,
            size -> VoxelShapingTool.fromBottomCenter(10 + 2 * size, 1),
            FacingLampBlock::new,
            (block, dyeColor) -> new ColoredBlockItem(block, new Item.Settings(), dyeColor)
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

    public record IdPack(Variant variant, Size size) {
        private String idString() {
            return variant().getId() + size().getId().map(p -> "_" + p).orElse("");
        }

        public Identifier blockId() {
            return LET.id("block", idString());
        }
    }

    public record Wrapper(Variant variant, Size size, DyeColor dyeColor) {
        public Identifier id() {
            return LET.id(variant().getId()
                    + size().getId().map(p -> "_" + p).orElse("")
                    + "_" + dyeColor().getName());
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

        public int luminance() {
            return variant().getLuminance(size().getSize());
        }

        public VoxelShape voxelShape() {
            return variant().getVoxelShape(size().getSize());
        }

        public Block block() {
            return variant().getBlock(this);
        }

        public BlockItem blockItem(Block block) {
            return variant().getBlockItem(block, dyeColor());
        }

        public Vec3d placeParticle(BlockPos blockPos, Random random, double factor) {
            return Variant.placeParticle(blockPos, voxelShape(), random, factor);
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
    }

    final String id;
    final int luminance;
    final Function<Integer, VoxelShape> voxelShapeProvider;
    final Function<Wrapper, Block> blockProvider;
    final BiFunction<Block, DyeColor, BlockItem> blockItemProvider;

    Variant(
            String id, int luminance,
            Function<Integer, VoxelShape> voxelShapeProvider,
            Function<Wrapper, Block> blockProvider,
            BiFunction<Block, DyeColor, BlockItem> blockItemProvider
    ) {
        this.id = id;
        this.luminance = luminance;
        this.voxelShapeProvider = voxelShapeProvider;
        this.blockProvider = blockProvider;
        this.blockItemProvider = blockItemProvider;
    }

    public IdPack with(Size size) {
        return new IdPack(this, size);
    }

    public Wrapper with(Size size, DyeColor dyeColor) {
        return new Wrapper(this, size, dyeColor);
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
}
