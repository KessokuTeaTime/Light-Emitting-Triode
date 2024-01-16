package band.kessokuteatime.lightemittingtriode;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.block.base.AbstractLampBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;

import java.util.ArrayList;
import java.util.Arrays;

public class LightEmittingTriodeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Arrays.stream(ModRegistries.Blocks.Type.values())
                .map(ModRegistries.Blocks.Type::wrappers)
                .flatMap(ArrayList::stream)
                .forEach(wrapper -> {
                    Block block = wrapper.block();
                    BlockItem item = wrapper.blockItem();

                    // Register tints
                    ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> wrapper.colorOverlay(
                            ((AbstractLampBlock) state.getBlock()).isLit(state), tintIndex
                    ), block);
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) -> wrapper.colorOverlay(false, 2), item);

                    // Make translucent
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                });
    }
}
