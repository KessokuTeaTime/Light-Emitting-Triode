package band.kessokuteatime.lightemittingtriode.content.block.base;

import band.kessokuteatime.lightemittingtriode.LET;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class AbstractLampBlock extends AbstractGlassBlock {
    protected final Variant.Wrapper wrapper;

    protected AbstractLampBlock(Settings settings, Variant.Wrapper wrapper) {
        super(settings);
        this.wrapper = wrapper;
    }

    protected Variant.Wrapper wrapper() {
        return wrapper;
    }

    public Consumer<Consumer<RecipeJsonProvider>> recipeBuilders() {
        return exporter -> {
            wrapper().useCraftingRecipeJsonBuilder().accept(exporter);
            wrapper().useUpgradingRecipeJsonBuilder().accept(exporter);
            wrapper().useRecoloringRecipeJsonBuilders().accept(exporter);
        };
    }

    public abstract boolean isLit(BlockState state);

    public abstract BiFunction<BlockStateModelGenerator, Block, BlockStateSupplier> generateBlockStates(ModRegistries.Blocks.Type type);

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (isLit(state)) {
            Vec3d particlePos = wrapper().placeParticle(pos, random, 1);
            world.addParticle(
                    new DustParticleEffect(LET.toColorArrayFloat(wrapper().colorOverlay(true, 1)), 0.85F),
                    particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                    0, 0, 0
            );
        }
    }
}
