package band.kessokuteatime.lightemittingtriode.forge;

import band.kessokuteatime.lightemittingtriode.LETExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class LETExpectPlatformImpl {
    /**
     * This is our actual method to {@link LETExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
