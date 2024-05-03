package tocraft.wwdatagen;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.wwdatagen.config.NBTStripper;
import tocraft.wwdatagen.config.WWDataGenConfig;
import tocraft.wwdatagen.data.DataLoader;
import tocraft.wwdatagen.data.DataSaver;
import tocraft.wwdatagen.util.TypeProviderHelper;

public class WWDataGen {
    public static final String MODID = "wwdatagen";
    public static final NBTStripper NBT_STRIPPER = ConfigLoader.read(NBTStripper.NAME, NBTStripper.class);
    public static final WWDataGenConfig CONFIG = ConfigLoader.read(WWDataGenConfig.NAME, WWDataGenConfig.class);

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
                        TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = TypeProviderHelper.generateFromNBT(world, entity.getType(), nbt);
                        if (typeProviderEntry != null) {
                            DataSaver.save(typeProviderEntry);
                        }
                    }
                }
            }

            if (CONFIG.autoLoadData) {
                for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                    TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = DataLoader.loadGeneratedTypeProvider(EntityType.getKey(entityType));
                    if (typeProviderEntry != null && (typeProviderEntry.requiredMod() == null || typeProviderEntry.requiredMod().isBlank() || Platform.isModLoaded(typeProviderEntry.requiredMod()))) {
                        TypeProviderRegistry.register((EntityType<LivingEntity>) entityType, (TypeProvider<LivingEntity>) typeProviderEntry.typeProvider());
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
}
