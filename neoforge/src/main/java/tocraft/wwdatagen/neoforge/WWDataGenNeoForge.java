package tocraft.wwdatagen.neoforge;

import net.neoforged.fml.common.Mod;
import tocraft.wwdatagen.WWDataGen;

@Mod(WWDataGen.MODID)
public class WWDataGenNeoForge {

    public WWDataGenNeoForge() {
        new WWDataGen().initialize();
    }
}
