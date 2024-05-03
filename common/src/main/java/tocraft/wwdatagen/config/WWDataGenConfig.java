package tocraft.wwdatagen.config;

import tocraft.craftedcore.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WWDataGenConfig implements Config {
    public static final String NAME = "wwdatagenerator";
    public boolean autoLoadData = false;

    @Override
    public String getName() {
        return NAME;
    }
}
