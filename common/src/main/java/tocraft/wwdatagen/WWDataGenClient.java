package tocraft.wwdatagen;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.HumanoidSkill;
import tocraft.wwdatagen.data.DataSaver;
import tocraft.wwdatagen.util.TypeProviderHelper;

@Environment(EnvType.CLIENT)
public class WWDataGenClient {
    @SuppressWarnings("unchecked")
    public void initialize() {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world -> {
            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                Entity entity = entityType.create(world);
                if (entity instanceof LivingEntity && SkillRegistry.has((LivingEntity) entity, HumanoidSkill.ID)) {
                    EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
                    if (renderer instanceof HumanoidMobRenderer<?, ?>) {
                        // TODO: add humanoid skill for entity type
                        Walkers.LOGGER.info(EntityType.getKey(entityType) + " should have the HumanoidSkill.");
                    }
                }
            }

            // trigger scan when saving the level
            for (Entity entity : world.entitiesForRendering()) {
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
        });
    }
}
