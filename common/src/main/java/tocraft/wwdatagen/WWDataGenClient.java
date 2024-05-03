package tocraft.wwdatagen;

import dev.architectury.event.events.client.ClientPlayerEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProviderRegistry;

import java.util.Arrays;
import java.util.List;

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
                        for (String s : WWDataGen.NBT_STRIPPER.generic) {
                            nbt.remove(s);
                        }
                        if (WWDataGen.NBT_STRIPPER.specific.containsKey(EntityType.getKey(entity.getType()).toString())) {
                            for (String s : WWDataGen.NBT_STRIPPER.specific.get(EntityType.getKey(entity.getType()).toString())) {
                                nbt.remove(s);
                            }
                        }
                        Walkers.LOGGER.warn(Arrays.toString(nbt.getAllKeys().toArray(String[]::new)));
                    }
                }
            }
        });
    }
}
