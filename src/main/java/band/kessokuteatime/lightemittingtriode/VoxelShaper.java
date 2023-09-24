package band.kessokuteatime.lightemittingtriode;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VoxelShaper {
    public static VoxelShape fromBottomCenter(double sizePixel, double thicknessPixel) {
        double size = sizePixel / 16, thickness = thicknessPixel / 16;

        return VoxelShapes.cuboid(
                0.5 - size / 2, 0, 0.5 - size / 2,
                0.5 + size / 2, thickness, 0.5 + size / 2
        );
    }

    public static VoxelShape mirror(VoxelShape voxelShape, Direction.Axis axis) {
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

    public static VoxelShape swapAxis(VoxelShape voxelShape, Direction.Axis from, Direction.Axis to) {
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

    public static VoxelShape swapAroundAxis(VoxelShape voxelShape, Direction.Axis axis) {
        ArrayList<Direction.Axis> otherAxis = Arrays.stream(Direction.Axis.values())
                .filter(a -> a != axis)
                .collect(Collectors.toCollection(ArrayList::new));
        assert otherAxis.size() == 2;
        return swapAxis(voxelShape, otherAxis.get(0), otherAxis.get(1));
    }

    public static VoxelShape rotate(VoxelShape voxelShape, Direction from, Direction to) {
        VoxelShape rotatedVoxelShape = swapAxis(voxelShape, from.getAxis(), to.getAxis());
        return from.getDirection() != to.getDirection() ? mirror(rotatedVoxelShape, to.getAxis()) : rotatedVoxelShape;
    }

    public static VoxelShape scaleOnDirection(VoxelShape voxelShape, Direction direction, double factor) {
        Vec3d min = new Vec3d(
                voxelShape.getMin(Direction.Axis.X),
                voxelShape.getMin(Direction.Axis.Y),
                voxelShape.getMin(Direction.Axis.Z)
        ), max = new Vec3d(
                voxelShape.getMax(Direction.Axis.X),
                voxelShape.getMax(Direction.Axis.Y),
                voxelShape.getMax(Direction.Axis.Z)
        );

        return switch (direction.getAxis()) {
            case X -> VoxelShapes.cuboid(
                    scaleWithSign(min.getX(), factor, direction.getDirection()), min.getY(), min.getZ(),
                    scaleWithSign(max.getX(), factor, direction.getDirection()), max.getY(), max.getZ()
            );
            case Y -> VoxelShapes.cuboid(
                    min.getX(), scaleWithSign(min.getY(), factor, direction.getDirection()), min.getZ(),
                    max.getX(), scaleWithSign(max.getY(), factor, direction.getDirection()), max.getZ()
            );
            case Z -> VoxelShapes.cuboid(
                    min.getX(), min.getY(), scaleWithSign(min.getZ(), factor, direction.getDirection()),
                    max.getX(), max.getY(), scaleWithSign(max.getZ(), factor, direction.getDirection())
            );
        };
    }

    private static double scaleWithSign(double value, double factor, Direction.AxisDirection sign) {
        return switch (sign) {
            case POSITIVE -> value * factor;
            case NEGATIVE -> 1 - (1 - value) * factor;
        };
    }
}
