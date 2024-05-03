package tocraft.wwdatagen;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.wwdatagen.config.NBTStripper;
import tocraft.wwdatagen.data.DataSaver;

import java.util.List;

public class WWDataGen {
    public static final String MODID = "wwdatagen";
    public static final NBTStripper NBT_STRIPPER = ConfigLoader.read(NBTStripper.NAME, NBTStripper.class);

    public void initialize() {
        // ensure the config is up-to-date
        NBTStripper normalNBTStripper = new NBTStripper();
        for (String s : normalNBTStripper.generic) {
            if (!NBT_STRIPPER.generic.contains(s))
                NBT_STRIPPER.generic.add(s);
        }
        NBT_STRIPPER.specific.putAll(normalNBTStripper.specific);
        NBT_STRIPPER.save();


        if (Platform.getEnvironment() == Env.CLIENT) new WWDataGenClient().initialize();
        DataSaver.initialize();
    }

    @SuppressWarnings("unsued")
    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void stripNBT(CompoundTag nbt) {
        for (String s : NBT_STRIPPER.generic) {
            nbt.remove(s);
        }

        if (nbt.contains("id")) {
            List<String> specific = NBT_STRIPPER.specific.get(nbt.getString("id"));
            for (String s : specific) {
                nbt.remove(s);
            }
        }

    }
}
