package tocraft.wwdatagen;

import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import tocraft.walkers.api.data.variants.AdvancedNBTEntries;
import tocraft.walkers.api.data.variants.NBTTypeProvider;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.wwdatagen.data.DataLoader;
import tocraft.wwdatagen.data.DataSaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class WWDataGenClient {
    @SuppressWarnings("unchecked")
    public void initialize() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (player != null) {
                List<LivingEntity> nearby = player.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), player, player.getBoundingBox().inflate(10));
                for (LivingEntity entity : nearby) {
                    if (!TypeProviderRegistry.hasProvider((EntityType<? extends LivingEntity>) entity.getType())) {
                        CompoundTag nbt = new CompoundTag();
                        entity.save(nbt);
                        WWDataGen.stripNBT(nbt);

                        if (!nbt.getAllKeys().isEmpty()) {
                            Map<Integer, Map<String, AdvancedNBTEntries.AdvancedNBT>> variantData = new HashMap<>();

                            TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = DataLoader.loadGeneratedTypeProvider(EntityType.getKey(entity.getType()));
                            if (typeProviderEntry != null) {
                                // TODO: Merge type providers by checking each variant and moving it into a new type provider
                                for (int i = 0; i <= typeProviderEntry.typeProvider().getRange(); i++) {
                                    CompoundTag tag = new CompoundTag();
                                    ((TypeProvider<LivingEntity>) typeProviderEntry.typeProvider()).create((EntityType<LivingEntity>) entity.getType(), player.level(), i).save(tag);
                                    WWDataGen.stripNBT(tag);
                                    int j = 0;
                                    for (Integer k : variantData.keySet()) {
                                        if (j > k) j = k;
                                    }

                                    variantData.put(j, new HashMap<>() {
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
                                    });
                                }
                            }
                            variantData.put(variantData.size(), new HashMap<>() {
                                {
                                    // TODO: add everything that's still in the "tag" in this map
                                    for (String key : nbt.getAllKeys()) {
                                        switch (nbt.getTagType(key)) {
                                            case Tag.TAG_STRING ->
                                                    put(key, new AdvancedNBTEntries.AdvancedNBT("STRING", nbt.getString(key)));
                                            case Tag.TAG_INT ->
                                                    put(key, new AdvancedNBTEntries.AdvancedNBT("INTEGER", nbt.getInt(key)));
                                            default ->
                                                    put(key, new AdvancedNBTEntries.AdvancedNBT("BOOLEAN", nbt.getBoolean(key)));
                                        }
                                    }
                                }
                            });
                            // save new type provider
                            DataSaver.save(new TypeProviderDataManager.TypeProviderEntry<>((EntityType<LivingEntity>) entity.getType(), EntityType.getKey(entity.getType()).getNamespace(), new NBTTypeProvider<>(0, variantData.size() - 1, Either.right(new AdvancedNBTEntries(variantData)), new HashMap<>())));
                        }
                    }
                }
            }
        });
    }
}
