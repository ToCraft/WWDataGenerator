package tocraft.wwdatagen;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.wwdatagen.config.NBTStripper;
import tocraft.wwdatagen.config.WWDataGenConfig;
import tocraft.wwdatagen.data.DataLoader;
import tocraft.wwdatagen.data.DataSaver;
import tocraft.wwdatagen.listener.EntityListener;

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

        EntityEvent.ADD.register(EntityListener::onCreation);

        LifecycleEvent.SERVER_LEVEL_LOAD.register(world -> {
            if (CONFIG.autoLoadData) {
                for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                    // load type provider
                    TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry = DataLoader.loadGeneratedTypeProvider(EntityType.getKey(entityType));
                    if (typeProviderEntry != null && (typeProviderEntry.requiredMod() == null || typeProviderEntry.requiredMod().isBlank() || Platform.isModLoaded(typeProviderEntry.requiredMod()))) {
                        TypeProviderRegistry.register((EntityType<LivingEntity>) entityType, (TypeProvider<LivingEntity>) typeProviderEntry.typeProvider());
                    }

                    // load skills
                    SkillDataManager.SkillList skillList = DataLoader.loadGeneratedSkillList(EntityType.getKey(entityType));
                    if (skillList != null && (skillList.requiredMod() == null || skillList.requiredMod().isBlank() || Platform.isModLoaded(skillList.requiredMod()))) {
                        for (EntityType<LivingEntity> type : skillList.entityTypes()) {
                            SkillRegistry.registerByType(type, skillList.skillList().stream().map(skill -> (ShapeSkill<LivingEntity>) skill).toList());
                        }
                        for (TagKey<EntityType<?>> entityTag : skillList.entityTags()) {
                            SkillRegistry.registerByTag(entityTag, skillList.skillList().stream().map(skill -> (ShapeSkill<LivingEntity>) skill).toList());
                        }
                    }
                }
            }
        });

        if (Platform.getEnvironment() == Env.CLIENT) new WWDataGenClient().initialize();
        DataSaver.initialize();
    }

    @SuppressWarnings("unused")
    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
