package band.kessokuteatime.lightemittingtriode.util.old;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class Util {

    public static VoxelShape box(int x1, int y1, int z1, int x2, int y2, int z2) {

        // make sure that the x1, y1, z1 given to VoxelShapes.cuboid are
        // smaller than x2, y2, z2, required my minecraft >=1.17
        return VoxelShapes.cuboid(
                Math.min(x1, x2) / 16d,
                Math.min(y1, y2) / 16d,
                Math.min(z1, z2) / 16d,
                Math.max(x1, x2) / 16d,
                Math.max(y1, y2) / 16d,
                Math.max(z1, z2) / 16d
        );
    }

    public static VoxelShape combine(VoxelShape a, VoxelShape b) {
        return VoxelShapes.combine(a, b, BooleanBiFunction.OR);
    }

    public static VoxelShape[] getVariants(int a, int b, int c, int d, int e, int f) {
        return new VoxelShape[] {
                box(a, b, c, d, e, f),
                box(a, 16 - b, c, d, 16 - e, f),
                box(a, c, b, d, f, e),
                box(a, c, 16 - b, d, f, 16 - e),
                box(b, a, c, e, d, f),
                box(16 - b, a, c, 16 - e, d, f)
        };
    }

    public static VoxelShape[] combineVariants(VoxelShape[] a, VoxelShape[] b) {
        VoxelShape[] shapes = new VoxelShape[6];

        for (int i = 0; i < 6; i ++) {
            shapes[i] = combine(a[i], b[i]);
        }

        return shapes;
    }

}
