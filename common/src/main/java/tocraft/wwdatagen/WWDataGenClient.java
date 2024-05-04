package tocraft.wwdatagen;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.EntityEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.HumanoidSkill;
import tocraft.wwdatagen.data.DataSaver;
import tocraft.wwdatagen.util.TypeProviderHelper;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WWDataGenClient {
    @SuppressWarnings("unchecked")
    public void initialize() {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world -> {
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

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (Minecraft.getInstance().level != null) {
                // scan for humanoid skill
                for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                    Entity entity = entityType.create(Minecraft.getInstance().level);
                    if (entity instanceof Mob && !SkillRegistry.has((LivingEntity) entity, HumanoidSkill.ID)) {
                        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
                        if (renderer instanceof HumanoidMobRenderer<?, ?>) {
                            DataSaver.save(new SkillDataManager.SkillList("", List.of(EntityType.getKey(entityType)), new ArrayList<>(), List.of(new HumanoidSkill<>())));
                        }
                    }
                }
            }
        });
    }
}
