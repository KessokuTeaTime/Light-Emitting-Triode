package band.kessokuteatime.lightemittingtriode;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class LightEmittingTriode implements ModInitializer {
    public static final String ID = "let", NAME = "Light Emitting Triode";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static String idString(String category, String... paths) {
        return category + "." + ID + "." + String.join(".", paths);
    }

    public static MutableText translatable(String category, String... paths) {
        return Text.translatable(idString(category, paths));
    }

    public static Identifier id(String... paths) {
        return new Identifier(ID, String.join("/", paths));
    }

    public static boolean isValidTool(ItemStack stack) {
        return stack.isIn(ItemTags.TOOLS) && !stack.isIn(ItemTags.SWORDS);
    }

    public static boolean isValidDye(ItemStack stack) {
        Item[] items = new Item[]{
                Items.WHITE_DYE, Items.ORANGE_DYE, Items.MAGENTA_DYE, Items.LIGHT_BLUE_DYE,
                Items.YELLOW_DYE, Items.LIME_DYE, Items.PINK_DYE, Items.GRAY_DYE,
                Items.LIGHT_GRAY_DYE, Items.CYAN_DYE, Items.PURPLE_DYE, Items.BLUE_DYE,
                Items.BROWN_DYE, Items.GREEN_DYE, Items.RED_DYE, Items.BLACK_DYE
        };

        return Arrays.stream(items).anyMatch(stack::isOf);
    }

    public static int getColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor.getSignColor();
    }

    public static DyeColor getDyeColorFromDye(Item item, DyeColor fallback) {
        Identifier identifier = Registries.ITEM.getId(item);
        if (!isValidDye(item.getDefaultStack())) {
            LOGGER.error(identifier + " is not a valid dye!");
            return fallback;
        }

        return DyeColor.byName(identifier.getPath().replace("_dye", ""), fallback);
    }

    public static int mapColorRange(int color, int preOffset, int postOffset) {
        int range = 0xFF - preOffset - postOffset;
        int r = (color & 0xFF0000) >> 16, g = (color & 0xFF00) >> 8, b = color & 0xFF;

        r = (int) (range * ((double) r / 0xFF) + preOffset);
        g = (int) (range * ((double) g / 0xFF) + preOffset);
        b = (int) (range * ((double) b / 0xFF) + preOffset);

        return (MathHelper.clamp(r, 0, 0xFF) << 16) + (MathHelper.clamp(g, 0, 0xFF) << 8) + MathHelper.clamp(b, 0, 0xFF);
    }

    public static Vector3f toColorArrayFloat(int color) {
        return new Vector3f(((color & 0xFF0000) >> 16) / 255F, ((color & 0xFF00) >> 8) / 255F, (color & 0xFF) / 255F);
    }

    public static Vec3d generateSurfaceParticlePos(BlockPos blockPos, VoxelShape voxelShape, Random random, double factor) {
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

    public static class Properties {
        public static final BooleanProperty DIM = BooleanProperty.of("dim");
        public static final BooleanProperty FULL = BooleanProperty.of("full");
    }

    @Override
    public void onInitialize() {
        ModRegistries.register();
    }
}
