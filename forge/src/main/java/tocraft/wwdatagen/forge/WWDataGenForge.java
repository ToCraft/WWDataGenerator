package tocraft.wwdatagen.forge;

import net.minecraftforge.fml.common.Mod;
import tocraft.wwdatagen.WWDataGen;

@Mod(WWDataGen.MODID)
public class WWDataGenForge {

    public WWDataGenForge() {
        new WWDataGen().initialize();
    }
}
