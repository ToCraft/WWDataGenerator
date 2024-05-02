package tocraft.wwdatagen.fabric;

import net.fabricmc.api.ModInitializer;
import tocraft.wwdatagen.WWDataGen;

public class WWDataGenFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        new WWDataGen().initialize();
    }
}
