package band.kessokuteatime.lightemittingtriode.fabric;

import band.kessokuteatime.lightemittingtriode.LETExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LETExpectPlatformImpl {
    /**
     * This is our actual method to {@link LETExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
