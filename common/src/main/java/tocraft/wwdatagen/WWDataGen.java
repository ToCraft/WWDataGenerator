package tocraft.wwdatagen;

import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.variants.AdvancedNBTEntries;
import tocraft.walkers.api.data.variants.NBTTypeProvider;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.wwdatagen.config.NBTStripper;
import tocraft.wwdatagen.data.DataLoader;
import tocraft.wwdatagen.data.DataSaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WWDataGen {
    public static final String MODID = "wwdatagen";
    public static final NBTStripper NBT_STRIPPER = ConfigLoader.read(NBTStripper.NAME, NBTStripper.class);

    @SuppressWarnings("unchecked")
    public void initialize() {
        // ensure the config is up-to-date
        NBTStripper normalNBTStripper = new NBTStripper();
        for (String s : normalNBTStripper.generic) {
            if (!NBT_STRIPPER.generic.contains(s))
                NBT_STRIPPER.generic.add(s);
        }
        NBT_STRIPPER.specific.putAll(normalNBTStripper.specific);
        NBT_STRIPPER.save();

        // trigger scan when saving the level
        LifecycleEvent.SERVER_LEVEL_SAVE.register(world -> {
            for (Entity entity : world.getAllEntities()) {
                if (entity instanceof LivingEntity) {
                    if (!TypeProviderRegistry.hasProvider((EntityType<? extends LivingEntity>) entity.getType())) {
                        CompoundTag nbt = new CompoundTag();
                        entity.save(nbt);
                        TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = WWDataGen.generateFromNBT(world, entity.getType(), nbt);
                        if (typeProviderEntry != null) {
                            DataSaver.save(typeProviderEntry);
                        }
                    }
                }
            }
        });

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
            String type = nbt.getString("id");
            if (NBT_STRIPPER.specific.containsKey(type)) {
                List<String> specific = NBT_STRIPPER.specific.get(type);
                for (String s : specific) {
                    nbt.remove(s);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static TypeProviderDataManager.TypeProviderEntry<?> generateFromNBT(Level level, EntityType<?> entityType, CompoundTag compoundTag) {
        CompoundTag nbt = compoundTag.copy();
        WWDataGen.stripNBT(nbt);

        if (!nbt.getAllKeys().isEmpty()) {
            Map<Integer, Map<String, AdvancedNBTEntries.AdvancedNBT>> variantData = new HashMap<>();

            TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = DataLoader.loadGeneratedTypeProvider(EntityType.getKey(entityType));
            if (typeProviderEntry != null) {
                // TODO: Merge type providers by checking each variant and moving it into a new type provider
                for (int i = 0; i <= typeProviderEntry.typeProvider().getRange(); i++) {
                    // if possible, generate every possible variant and gain NBT from them
                    CompoundTag tag = new CompoundTag();
                    ((TypeProvider<LivingEntity>) typeProviderEntry.typeProvider()).create((EntityType<LivingEntity>) entityType, level, i).save(tag);
                    WWDataGen.stripNBT(tag);

                    Map<String, AdvancedNBTEntries.AdvancedNBT> data = new HashMap<>() {
                        {
                            // TODO: add everything that's still in the "tag" in this map
                            for (String key : tag.getAllKeys()) {
                                switch (tag.getTagType(key)) {
                                    case Tag.TAG_STRING ->
                                            put(key, new AdvancedNBTEntries.AdvancedNBT("STRING", tag.getString(key)));
                                    case Tag.TAG_INT ->
                                            put(key, new AdvancedNBTEntries.AdvancedNBT("INTEGER", tag.getInt(key)));
                                    default ->
                                            put(key, new AdvancedNBTEntries.AdvancedNBT("BOOLEAN", tag.getBoolean(key)));
                                }
                            }
                        }
                    };

                    if (canPutInVariantData(variantData, data)) {
                        variantData.put(i, data);
                    }
                }
            }
            Map<String, AdvancedNBTEntries.AdvancedNBT> data = new HashMap<>() {
                {
                    // TODO: add everything that's still in the "tag" in this map
                    for (String key : nbt.getAllKeys()) {
                        switch (nbt.getTagType(key)) {
                            case Tag.TAG_STRING ->
                                    put(key, new AdvancedNBTEntries.AdvancedNBT("STRING", nbt.getString(key)));
                            case Tag.TAG_INT ->
                                    put(key, new AdvancedNBTEntries.AdvancedNBT("INTEGER", nbt.getInt(key)));
                            default -> put(key, new AdvancedNBTEntries.AdvancedNBT("BOOLEAN", nbt.getBoolean(key)));
                        }
                    }
                }
            };
            if (canPutInVariantData(variantData, data)) {
                // the highest registered value
                int i = 0;
                for (Integer key : variantData.keySet()) {
                    if (key > i) i = key;
                }
                variantData.put(i + 1, data);
            }

            if (!variantData.isEmpty()) {
                // add optional requiredMod field
                String requiredMod = EntityType.getKey(entityType).getNamespace();
                if (requiredMod.equals("minecraft")) requiredMod = "";
                return new TypeProviderDataManager.TypeProviderEntry<>((EntityType<LivingEntity>) entityType, requiredMod, new NBTTypeProvider<>(0, variantData.size() - 1, Either.right(new AdvancedNBTEntries(variantData)), new HashMap<>()));
            }
        }
        return null;
    }

    private static boolean canPutInVariantData(Map<Integer, Map<String, AdvancedNBTEntries.AdvancedNBT>> variantData, Map<String, AdvancedNBTEntries.AdvancedNBT> nbtData) {
        return !nbtData.isEmpty() && variantData.values().stream().noneMatch(entry -> {
            boolean bool = true;
            for (Map.Entry<String, AdvancedNBTEntries.AdvancedNBT> dataEntry : nbtData.entrySet()) {
                bool = entry.containsKey(dataEntry.getKey()) && Objects.equals(entry.get(dataEntry.getKey()).value(), dataEntry.getValue().value());
                if (!bool) {
                    break;
                }
            }
            return bool;
        });
    }
}
