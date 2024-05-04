package tocraft.wwdatagen.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.data.variants.AdvancedNBTEntries;
import tocraft.walkers.api.data.variants.NBTTypeProvider;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.wwdatagen.config.NBTStripper;
import tocraft.wwdatagen.data.DataLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TypeProviderHelper {
    @SuppressWarnings("unchecked")
    @Nullable
    public static TypeProviderDataManager.TypeProviderEntry<?> generateFromNBT(Level level, EntityType<?> entityType, CompoundTag compoundTag) {
        CompoundTag nbt = compoundTag.copy();
        NBTStripper.stripNBT(EntityType.getKey(entityType), nbt);

        if (!nbt.getAllKeys().isEmpty()) {
            List<CompoundTag> variantData = new ArrayList<>();

            TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = DataLoader.loadGeneratedTypeProvider(EntityType.getKey(entityType));
            if (typeProviderEntry != null) {
                for (int i = 0; i <= typeProviderEntry.typeProvider().getRange(); i++) {
                    // if possible, generate every possible variant and gain NBT from them
                    CompoundTag tag = new CompoundTag();
                    ((TypeProvider<LivingEntity>) typeProviderEntry.typeProvider()).create((EntityType<LivingEntity>) entityType, level, i).save(tag);
                    NBTStripper.stripNBT(EntityType.getKey(entityType), tag);

                    CompoundTag data = new CompoundTag();
                    for (String key : tag.getAllKeys()) {
                        data.put(key, tag.get(key));
                    }

                    if (canPutInVariantData(variantData, data)) {
                        variantData.add(i, data);
                    }
                }
            }
            CompoundTag data = new CompoundTag();
            for (String key : nbt.getAllKeys()) {
                data.put(key, nbt.get(key));
            }

            if (canPutInVariantData(variantData, data)) {
                variantData.add(variantData.size(), data);
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

    private static boolean canPutInVariantData(List<CompoundTag> variantData, CompoundTag nbtData) {
        return !nbtData.isEmpty() && variantData.stream().noneMatch(entry -> {
            int matches = 0;
            for (String key : nbtData.getAllKeys()) {
                if (entry.contains(key) && entry.get(key) == nbtData.get(key)) {
                    matches++;
                }
            }
            return matches < nbtData.getAllKeys().size();
        });
    }
}
