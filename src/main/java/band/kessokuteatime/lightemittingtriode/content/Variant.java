package band.kessokuteatime.lightemittingtriode.content;

import band.kessokuteatime.lightemittingtriode.LET;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public enum Variant {
    CEILING("ceiling", 10,
            size -> fromBottomCenter(16, 1)
    ),
    SLAB("slab", 10,
            size -> fromBottomCenter(16, 8)
    ),
    CLEAR("clear", 7,
            size -> VoxelShapes.fullCube()
    ),
    LANTERN("lantern", 5,
            size -> fromBottomCenter(4 + 2 * size, 6 + size)
    ),
    ALARM("alarm", 3,
            size -> fromBottomCenter(10 + 2 * size, 4 + size)
    );

    private static VoxelShape fromBottomCenter(double sizePixel, double thicknessPixel) {
        double size = sizePixel / 16, thickness = thicknessPixel / 16;

        return VoxelShapes.cuboid(
                0.5 - size / 2, 0, 0.5 - size / 2,
                0.5 + size / 2, thickness, 0.5 + size / 2
        );
    }

    public record Wrapper(Variant variant, Size size) {
        public Identifier id(DyeColor dyeColor) {
            return LET.id(variant().getId()
                    + size().getId().map(p -> "_" + p).orElse("")
                    + "_" + dyeColor.getName());
        }

        private String idString() {
            return variant().getId() + size().getId().map(p -> "_" + p).orElse("");
        }

        public Identifier blockId() {
            return LET.id("block", idString());
        }

        public Identifier blockGlowId() {
            return LET.id("block", "glow", idString());
        }

        public int luminance() {
            return variant().getLuminance(size().getSize());
        }

        public VoxelShape voxelShape() {
            return variant().getVoxelShape(size().getSize());
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
    final Function<Integer, VoxelShape> voxelShapeFunction;

    Variant(String id, int luminance, Function<Integer, VoxelShape> voxelShapeFunction) {
        this.id = id;
        this.luminance = luminance;
        this.voxelShapeFunction = voxelShapeFunction;
    }

    public Wrapper with(Size size) {
        return new Wrapper(this, size);
    }

    public String getId() {
        return id;
    }

    public int getLuminance(int size) {
        return luminance + 2 * size + 1;
    }

    public VoxelShape getVoxelShape(int size) {
        return voxelShapeFunction.apply(size);
    }
}
