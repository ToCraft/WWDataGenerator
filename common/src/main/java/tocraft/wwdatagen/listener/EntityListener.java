package tocraft.wwdatagen.listener;

import dev.architectury.event.EventResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.wwdatagen.data.DataSaver;
import tocraft.wwdatagen.util.TypeProviderHelper;

public class EntityListener {
    @SuppressWarnings("unchecked")
    public static EventResult onCreation(Entity entity, Level world) {
        if (entity instanceof LivingEntity) {
            if (!TypeProviderRegistry.hasProvider((EntityType<? extends LivingEntity>) entity.getType())) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = TypeProviderHelper.generateFromNBT(world, entity.getType(), nbt);
                if (typeProviderEntry != null) {
                    DataSaver.save(typeProviderEntry);
                }
            }
        }

        return EventResult.pass();
    }
}
