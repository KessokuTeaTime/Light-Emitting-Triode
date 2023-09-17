package band.kessokuteatime.lightemittingtriode.fabric;

import band.kessokuteatime.lightemittingtriode.LET;
import net.fabricmc.api.ModInitializer;

public class LETFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LET.init();
    }
}
