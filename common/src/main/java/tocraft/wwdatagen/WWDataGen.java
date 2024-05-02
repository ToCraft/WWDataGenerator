package tocraft.wwdatagen;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;

public class WWDataGen {
    public static final String MODID = "wwdatagen";

    public void initialize() {
        if (Platform.getEnvironment() == Env.CLIENT) new WWDataGenClient().initialize();
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
