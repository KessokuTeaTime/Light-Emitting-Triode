package band.kessokuteatime.lightemittingtriode.forge;

import band.kessokuteatime.lightemittingtriode.LET;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LET.ID)
public class LETForge {
    public LETForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(LET.ID, FMLJavaModLoadingContext.get().getModEventBus());
        LET.init();
    }
}
