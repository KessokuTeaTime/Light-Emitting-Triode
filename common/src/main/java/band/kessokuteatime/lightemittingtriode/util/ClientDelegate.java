package band.kessokuteatime.lightemittingtriode.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class ClientDelegate {

    private final Block block;
    private final Item item;
    private final DyeColor color;

    public ClientDelegate(DyeColor color, Block block, Item item) {
        this.color = color;
        this.block = block;
        this.item = item;
    }

    @Environment(EnvType.CLIENT)
    public void register() {
        ColorProviderRegistry.BLOCK.register(this::blockProvider, block);
        ColorProviderRegistry.ITEM.register(this::itemProvider, item);
    }

    @Environment(EnvType.CLIENT)
    private int blockProvider(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
        return color.getFireworkColor();
    }

    @Environment(EnvType.CLIENT)
    private int itemProvider(ItemStack stack, int tintIndex) {
        return color.getFireworkColor();
    }

}
