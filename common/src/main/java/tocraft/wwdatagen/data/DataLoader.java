package tocraft.wwdatagen.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static tocraft.wwdatagen.data.DataManager.createDirectories;

@SuppressWarnings("unused")
public final class DataLoader {
    @Nullable
    public static TypeProviderDataManager.TypeProviderEntry<?> loadGeneratedTypeProvider(ResourceLocation entityType) {
        Path path = DataManager.getGeneratedTypeProviderPath(entityType);
        JsonElement json = readJson(path);
        if (json != null && !json.isJsonNull()) {
            Either<TypeProviderDataManager.TypeProviderEntry<?>, String> typeProviderEntryStringEither = Util.getOrThrow(TypeProviderDataManager.TYPE_PROVIDER_LIST_CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new);
            // print error
            if (typeProviderEntryStringEither.right().isPresent()) {
                Walkers.LOGGER.warn(String.format(typeProviderEntryStringEither.right().get(), path));
            } else if (typeProviderEntryStringEither.left().isPresent()) {
                return typeProviderEntryStringEither.left().get();
            }
        }
        return null;
    }

    @Nullable
    public static SkillDataManager.SkillList loadGeneratedSkillList(ResourceLocation entityType) {
        for (Path path : DataManager.SKILLS_PATH) {
            if (path.endsWith(".json")) {
                JsonElement json = readJson(path);
                if (json != null && !json.isJsonNull()) {
                    SkillDataManager.SkillList skillList = Util.getOrThrow(SkillDataManager.SKILL_LIST_CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new);
                    if (skillList.entityTypeKeys().contains(entityType)) {
                        return skillList;
                    }
                }
            }
        }
        return null;
    }

    private static JsonElement readJson(Path file) {
        try {
            createDirectories();

            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            StringBuilder json = new StringBuilder();
            String line;
            BufferedReader updateReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8));
            while ((line = updateReader.readLine()) != null && !(line = line.replaceAll("\n", "").replaceAll("\r", "")).isBlank()) {
                json.append(line);
            }
            updateReader.close();

            if (!json.toString().isBlank()) {
                return JsonParser.parseString(json.toString());
            }

        } catch (IOException e) {
            LogUtils.getLogger().error("Couldn't save config at {}. {}", file.toAbsolutePath(), e);
        }
        return JsonOps.INSTANCE.empty();
    }
}
