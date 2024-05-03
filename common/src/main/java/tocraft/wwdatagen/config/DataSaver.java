package tocraft.wwdatagen.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public final class DataSaver {
    private static final Path GENERATED_PATH = Paths.get(Platform.getConfigFolder().toString(), "walkers/generated");
    private static final Path VARIANTS_PATH = Paths.get(GENERATED_PATH.toString(), "data/auto/alkers/variants");
    private static final Path SKILLS_PATH = Paths.get(GENERATED_PATH.toString(), "data/auto/alkers/skills");
    private static final String PACK_MCMETA = "{\n" +
            "\t\"pack\": {\n" +
            "\t\t\"description\": \"Generated Walkers Datapack\",\n" +
            "\t\t\"pack_format\": 6\n" +
            "\t}\n" +
            "}";

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
        if (!skillList.entityTypeKeys().isEmpty()) fileName += "_" + skillList.entityTypeKeys().get(0).getPath();
        if (!skillList.entityTagKeys().isEmpty()) fileName += "_" + skillList.entityTagKeys().get(0).getPath();
        write(Paths.get(SKILLS_PATH.toString(), fileName + ".json"), json);
    }

    private static void createDirectories() throws IOException {
        if (!Files.exists(GENERATED_PATH)) {
            Files.createDirectories(GENERATED_PATH);
        }

        if (!Files.exists(VARIANTS_PATH)) {
            Files.createDirectories(VARIANTS_PATH);
        }

        if (!Files.exists(SKILLS_PATH)) {
            Files.createDirectories(SKILLS_PATH);
        }
    }

    private static void write(Path file, JsonElement json) {
        // the file should be readable, therefore the GsonBuilder
        write(file, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }

    private static void write(Path file, String content) {
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
