package band.kessokuteatime.lightemittingtriode.content.block.functional;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;

import java.util.function.BiFunction;

public class DetectorLampBlock extends FacingPowerableLampBlock {
    public DetectorLampBlock(Variant.Wrapper wrapper) {
        super(wrapper);
    }
}
