package tocraft.wwdatagen.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import tocraft.craftedcore.config.Config;
import tocraft.wwdatagen.WWDataGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTStripper implements Config {
    public static final String NAME = "NBTStripper";
    public List<String> generic = new ArrayList<>() {
        {
            add("FallDistance");
            add("DeathTime");
            add("Attributes");
            add("HurtByTimestamp");
            add("Brain");
            add("ArmorItems");
            add("HurtTime");
            add("Inventory");
            add("PortalCooldown");
            add("TimeInOverworld");
            add("id");
            add("UUID");
            add("Air");
            add("OnGround");
            add("Rotation");
            add("HandItems");
            add("ArmorDropChances");
            add("Pos");
            add("Fire");
            add("CanPickUpLoot");
            add("Invulnerable");
            add("FallFlying");
            add("AbsorptionAmount");
            add("HandDropChances");
            add("PersistenceRequired");
            add("Motion");
            add("Health");
            add("LeftHanded");
            add("CustomName");
            add("InLove");
            add("CanBreakDoors");
            add("InWaterTime");
            add("AngerTime");
            add("DrownedConversionTime");
            add("StrayConversionTime");
            add("IsBaby");
            add("ForcedAge");
            add("Age");
            add("FromBucket");
        }
    };
    public Map<String, List<String>> specific = new HashMap<>() {
        {
            put("minecraft:wolf", List.of("isSpecial"));
            put("minecraft:bat", List.of("BatFlags"));
            put("minecraft:chicken", List.of("IsChickenJockey", "EggLayTime"));
            put("minecraft:creeper", List.of("Fuse", "ignited", "ExplosionRadius"));
            put("minecraft:dolphin", List.of("TreasurePosZ", "TreasurePosY", "TreasurePosX", "Moistness", "GotFish"));
            put("minecraft:glow_squid", List.of("DarkTicksRemaining"));
            put("minecraft:pig", List.of("Saddle"));
        }
    };

    public static void stripNBT(ResourceLocation entityType, CompoundTag nbt) {
        for (String s : WWDataGen.NBT_STRIPPER.generic) {
            nbt.remove(s);
        }

        if (WWDataGen.NBT_STRIPPER.specific.containsKey(entityType.toString())) {
            List<String> specific = WWDataGen.NBT_STRIPPER.specific.get(entityType.toString());
            for (String s : specific) {
                nbt.remove(s);
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
