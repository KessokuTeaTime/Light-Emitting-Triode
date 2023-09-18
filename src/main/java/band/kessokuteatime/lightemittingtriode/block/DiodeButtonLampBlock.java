package band.kessokuteatime.lightemittingtriode.block;

import band.kessokuteatime.lightemittingtriode.util.DiodeVariant;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.ButtonBlock;
import net.minecraft.sound.SoundEvents;

public class DiodeButtonLampBlock extends ButtonBlock {
    public DiodeButtonLampBlock() {
        super(DiodeVariant.NORMAL.settings()
                .luminance((state) -> state.get(POWERED) ? 3 : 0)
                .emissiveLighting((state, world, pos) -> state.get(POWERED)),
                BlockSetType.GOLD,
                20, false
        );
    }
}
