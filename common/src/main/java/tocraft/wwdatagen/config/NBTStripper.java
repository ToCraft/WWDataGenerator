package tocraft.wwdatagen.config;

import tocraft.craftedcore.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTStripper implements Config {
    public static final String NAME = "NBTStripper";
    public List<String> generic = new ArrayList<>() {
        {
            add("Age");
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
        }
    };
    public Map<String, List<String>> specific = new HashMap<>() {
        {
            put("minecraft:wolf", List.of("isSpecial"));
        }
    };

    @Override
    public String getName() {
        return NAME;
    }
}
