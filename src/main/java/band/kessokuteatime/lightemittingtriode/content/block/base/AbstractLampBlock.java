package band.kessokuteatime.lightemittingtriode.content.block.base;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.block.base.extension.WithCustomBlockRecipe;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.function.Consumer;

public abstract class AbstractLampBlock extends AbstractGlassBlock implements WithCustomBlockRecipe, OfAnotherColor {
    protected final Wrapper wrapper;

    protected AbstractLampBlock(Wrapper wrapper) {
        super(wrapper.buildSettings());
        this.wrapper = wrapper;
    }

    protected Wrapper wrapper() {
        return wrapper;
    }

    public DyeColor dyeColor() {
        return wrapper().dyeColor();
    }

    @Override
    public Consumer<Consumer<RecipeJsonProvider>> generateRecipe() {
        return exporter -> {
            wrapper().useCraftingRecipeJsonBuilder().accept(exporter);
            wrapper().useUpgradingRecipeJsonBuilder().accept(exporter);
            wrapper().useRecoloringRecipeJsonBuilders().accept(exporter);
        };
    }

    @Override
    public BlockState ofAnotherColor(BlockState state, DyeColor dyeColor) {
        return Wrapper.block(wrapper().basis(), dyeColor).getDefaultState();
    }

    public abstract boolean isLit(BlockState state);

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (isLit(state)) {
            Vec3d particlePos = wrapper().generateSurfaceParticlePos(state, world, pos, null, random, 1);
            world.addParticle(
                    new DustParticleEffect(LightEmittingTriode.toColorArrayFloat(wrapper().colorOverlay(true, 1)), 0.6F),
                    particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                    0, 0, 0
            );
        }
    }
}
