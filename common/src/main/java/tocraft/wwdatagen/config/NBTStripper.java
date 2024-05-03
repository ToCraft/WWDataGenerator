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
            add("Invisible");
            add("Pose");
            add("LastPoseTick");
            add("Bred");
            add("Tame");
            add("Temper");
            add("EatingHaystack");
            add("Sleeping");
            add("Crouching");
            add("Sitting");
            add("wasOnGround");
            add("AttackTick");
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
            put("minecraft:armor_stand", List.of("DisabledSlots", "Small", "NoBasePlate", "ShowArms"));
            put("minecraft:allay", List.of("DuplicationCooldown", "listener", "CanDuplicate"));
            put("minecraft:bee", List.of("CropsGrownSincePollination", "CannotEnterHiveTicks", "TicksSincePollination"));
            put("minecraft:donkey", List.of("ChestedHorse"));
            put("minecraft:ender_dragon", List.of("DragonPhase", "DragonDeathTime"));
            put("minecraft:endermite", List.of("Lifetime"));
            put("minecraft:evoker", List.of("Wave", "CanJoinRaid", "SpellTicks", "PatrolLeader", "Patrolling"));
            put("minecraft:fox", List.of("Trusted"));
            put("minecraft:ghast", List.of("ExplosionPower"));
            put("minecraft:illusioner", List.of("Wave", "CanJoinRaid", "PatrolLeader", "SpellTicks", "Patrolling"));
            put("minecraft:iron_golem", List.of("PlayerCreated"));
            put("minecraft:llama", List.of("ChestedHorse", "Strength"));
            put("minecraft:mule", List.of("ChestedHorse"));
            put("minecraft:ocelot", List.of("Trusting"));
            put("minecraft:phantom", List.of("Size", "AX", "AY", "AZ"));
            put("minecraft:pillager", List.of("Wave", "CanJoinRaid", "PatrolLeader", "Patrolling"));
            put("minecraft:rabbit", List.of("MoreCarrotTicks"));
            put("minecraft:ravager", List.of("Wave", "RoarTick", "CanJoinRaid", "StunTick", "PatrolLeader", "Patrolling"));
            put("minecraft:shulker", List.of("Peek", "ChestedHorse"));
            put("minecraft:skeleton_horse", List.of("SkeletonTrapTime", "SkeletonTrap"));
            put("minecraft:strider", List.of("Saddle"));
            put("minecraft:trader_llama", List.of("ChestedHorse", "Strength", "DespawnDelay"));
            put("minecraft:turtle", List.of("HasEgg", "TravelPosZ", "HomePosZ", "TravelPosY", "TravelPosX", "HomePosX", "HomePosY"));
            put("minecraft:villager", List.of("Xp", "LastGossipDecay", "FoodLevel", "VillagerData", "Gossips", "LastRestock", "RestocksToday"));
            put("minecraft:vindicator", List.of("Wave", "CanJoinRaid", "PatrolLeader", "Patrolling"));
            put("minecraft:wandering_trader", List.of("DespawnDelay", "Offers"));
            put("minecraft:warden", List.of("listener", "anger"));
            put("minecraft:witch", List.of("Wave", "CanJoinRaid", "PatrolLeader", "Patrolling"));
            put("minecraft:wither", List.of("Invul"));
            put("minecraft:zombie_villager", List.of("ConversionTime", "Xp", "VillagerData"));
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
