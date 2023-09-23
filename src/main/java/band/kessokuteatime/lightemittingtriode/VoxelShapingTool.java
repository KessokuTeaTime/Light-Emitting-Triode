package band.kessokuteatime.lightemittingtriode;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Vector2d;

public class VoxelShapingTool {
    public static VoxelShape fromBottomCenter(double sizePixel, double thicknessPixel) {
        double size = sizePixel / 16, thickness = thicknessPixel / 16;

        return VoxelShapes.cuboid(
                0.5 - size / 2, 0, 0.5 - size / 2,
                0.5 + size / 2, thickness, 0.5 + size / 2
        );
    }

    public static VoxelShape mirrorVoxelShape(VoxelShape voxelShape, Direction.Axis axis) {
        double
                xMin = voxelShape.getMin(Direction.Axis.X), xMax = voxelShape.getMax(Direction.Axis.X),
                yMin = voxelShape.getMin(Direction.Axis.Y), yMax = voxelShape.getMax(Direction.Axis.Y),
                zMin = voxelShape.getMin(Direction.Axis.Z), zMax = voxelShape.getMax(Direction.Axis.Z);

        return switch (axis) {
            case X -> VoxelShapes.cuboid(
                    1 - xMax, yMin, zMin,
                    1 - xMin, yMax, zMax
            );
            case Y -> VoxelShapes.cuboid(
                    xMin, 1 - yMax, zMin,
                    xMax, 1 - yMin, zMax
            );
            case Z -> VoxelShapes.cuboid(
                    xMin, yMin, 1 - zMax,
                    xMax, yMax, 1 - zMin
            );
        };
    }

    public static VoxelShape swapVoxelShapeAxis(VoxelShape voxelShape, Direction.Axis from, Direction.Axis to) {
        Vec3d min = new Vec3d(
                voxelShape.getMin(Direction.Axis.X),
                voxelShape.getMin(Direction.Axis.Y),
                voxelShape.getMin(Direction.Axis.Z)
        ), max = new Vec3d(
                voxelShape.getMax(Direction.Axis.X),
                voxelShape.getMax(Direction.Axis.Y),
                voxelShape.getMax(Direction.Axis.Z)
        );

        String pattern = from.asString() + to.asString();

        return switch (pattern) {
            case "xy", "yx" ->VoxelShapes.cuboid(
                    min.getY(), min.getX(), min.getZ(),
                    max.getY(), max.getX(), max.getZ()
            );

            case "xz", "zx" -> VoxelShapes.cuboid(
                    min.getZ(), min.getY(), min.getX(),
                    max.getZ(), max.getY(), max.getX()
            );

            case "yz", "zy" -> VoxelShapes.cuboid(
                    min.getX(), min.getZ(), min.getY(),
                    max.getX(), max.getZ(), max.getY()
            );

            default -> voxelShape;
        };
    }

    public static VoxelShape rotateVoxelShape(VoxelShape voxelShape, Direction from, Direction to) {
        VoxelShape rotatedVoxelShape = swapVoxelShapeAxis(voxelShape, from.getAxis(), to.getAxis());
        return from.getDirection() != to.getDirection() ? mirrorVoxelShape(rotatedVoxelShape, to.getAxis()) : rotatedVoxelShape;
    }
}
