package tocraft.wwdatagen.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataSaver {
    private static final Path GENERATED_PATH = Paths.get(Platform.getConfigFolder().toString(), "walkers/generated");


    public static void save(TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry) {
        JsonElement json = TypeProviderDataManager.TYPE_PROVIDER_LIST_CODEC.encodeStart(JsonOps.INSTANCE, Either.left(typeProviderEntry)).get().orThrow();
        write(Paths.get(GENERATED_PATH.toString(), typeProviderEntry.entityTypeKey().getNamespace() + "_" + typeProviderEntry.entityTypeKey().getPath() + ".json"), json);
    }

    private static void write(Path file, JsonElement json) {
        try {
            if (!Files.exists(GENERATED_PATH)) {
                Files.createDirectories(GENERATED_PATH);
            }

            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            // the file should be readable, therefore the GsonBuilder
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(json));
        } catch (IOException e) {
            LogUtils.getLogger().error("Couldn't save config at {}. {}", file.toAbsolutePath(), e);
        }
    }
}
