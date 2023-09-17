package band.kessokuteatime.lightemittingtriode.block;

import band.kessokuteatime.lightemittingtriode.util.DiodeVariant;
import net.minecraft.block.ButtonBlock;
import net.minecraft.sound.SoundEvents;

public class DiodeButtonLampBlock extends ButtonBlock {

    public DiodeButtonLampBlock() {
        super(DiodeVariant.NORMAL.settings()
                .luminance((state) -> state.get(POWERED) ? 3 : 0)
                .emissiveLighting((state, world, pos) -> state.get(POWERED)),
                20, false, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
        );
    }

}
