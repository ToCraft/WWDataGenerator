package tocraft.wwdatagen.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static tocraft.wwdatagen.data.DataManager.*;

@SuppressWarnings("unused")
public final class DataSaver {
    private static final String PACK_MCMETA = """
            {
            \t"pack": {
            \t\t"description": "Generated Walkers Datapack",
            \t\t"pack_format": 6
            \t}
            }""";

    public static void initialize() {
        write(Paths.get(GENERATED_PATH.toString(), "pack.mcmeta"), PACK_MCMETA);
    }

    public static void save(TypeProviderDataManager.TypeProviderEntry<?> typeProviderEntry) {
        JsonElement json = TypeProviderDataManager.TYPE_PROVIDER_LIST_CODEC.encodeStart(JsonOps.INSTANCE, Either.left(typeProviderEntry)).get().orThrow();
        write(Paths.get(VARIANTS_PATH.toString(), typeProviderEntry.entityTypeKey().getNamespace() + "_" + typeProviderEntry.entityTypeKey().getPath() + ".json"), json);
    }

    public static void save(SkillDataManager.SkillList skillList) {
        JsonElement json = SkillDataManager.SKILL_LIST_CODEC.encodeStart(JsonOps.INSTANCE, skillList).get().orThrow();
        String fileName = skillList.requiredMod();
        if (!skillList.entityTypeKeys().isEmpty())
            fileName += (!Objects.equals(fileName, "") ? "_" : "") + skillList.entityTypeKeys().get(0).toString().replaceAll(":", "_");
        if (!skillList.entityTagKeys().isEmpty())
            fileName += (!Objects.equals(fileName, "") ? "_" : "") + skillList.entityTagKeys().get(0).toString().replaceAll(":", "_");
        write(Paths.get(SKILLS_PATH.toString(), fileName + ".json"), json);
    }

    private static void write(Path file, JsonElement json) {
        // the file should be readable, therefore the GsonBuilder
        if (json.isJsonObject()) {
            write(file, new GsonBuilder().setPrettyPrinting().create().toJson(sortJson2(json.getAsJsonObject())));
        } else {
            write(file, new GsonBuilder().setPrettyPrinting().create().toJson(json));
        }
    }

    // You don't want to know how long it took me to remember, that I can call a method in itself...
    private static JsonObject sortJson2(JsonObject parent) {
        JsonObject newJson = new JsonObject();
        parent.getAsJsonObject().entrySet().stream()
                // can one do more useless stuff? I just wanted alphabetically sorted JSONs...
                .sorted(Map.Entry.<String, JsonElement>comparingByKey().thenComparing(a -> a.getValue().getAsString()))
                .forEachOrdered(entry -> {
                    if (entry.getValue().isJsonObject()) {
                        JsonObject newJsonEntry = sortJson2(entry.getValue().getAsJsonObject());
                        newJson.getAsJsonObject().add(entry.getKey(), newJsonEntry);
                    } else {
                        newJson.getAsJsonObject().add(entry.getKey(), entry.getValue());
                    }

                });
        return newJson;
    }

    private static void write(Path file, String content) {
        if (content != null && !content.isBlank()) {
            try {
                createDirectories();

                if (!Files.exists(file)) {
                    Files.createFile(file);
                }

                Files.writeString(file, content);
            } catch (IOException e) {
                LogUtils.getLogger().error("Couldn't save config at {}. {}", file.toAbsolutePath(), e);
            }
        }
    }
}
