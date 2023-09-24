package band.kessokuteatime.lightemittingtriode.content.block.base;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public interface Facing {
    default boolean canPlaceAt(Direction direction, WorldView world, BlockPos pos) {
        return switch (direction) {
            case DOWN -> Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN);
            case UP -> Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
            case NORTH -> Block.sideCoversSmallSquare(world, pos.south(), Direction.NORTH);
            case SOUTH -> Block.sideCoversSmallSquare(world, pos.north(), Direction.SOUTH);
            case WEST -> Block.sideCoversSmallSquare(world, pos.east(), Direction.WEST);
            case EAST -> Block.sideCoversSmallSquare(world, pos.west(), Direction.EAST);
        };
    }
}
