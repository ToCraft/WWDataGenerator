package tocraft.wwdatagen.mixin;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.wwdatagen.listener.EntityListener;

@Mixin(EntityStorage.class)
public abstract class EntityStorageMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "storeEntities", at = @At(value = "HEAD"))
    private void onEntitySave(ChunkEntities<Entity> entities, CallbackInfo ci) {
        entities.getEntities().forEach(entity -> EntityListener.createData(entity, this.level));
    }
}
