package tocraft.wwdatagen;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;
import tocraft.wwdatagen.config.DataSaver;

public class WWDataGen {
    public static final String MODID = "wwdatagen";

    public void initialize() {
        if (Platform.getEnvironment() == Env.CLIENT) new WWDataGenClient().initialize();
        DataSaver.initialize();
    }

    @SuppressWarnings("unsued")
    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
