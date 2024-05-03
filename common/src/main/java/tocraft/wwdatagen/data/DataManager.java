package tocraft.wwdatagen.data;

import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public final class DataManager {
    public static final Path GENERATED_PATH = Paths.get(Platform.getConfigFolder().toString(), "walkers/generated");
    public static final Path DATA_PATH = Paths.get(GENERATED_PATH.toString(), "data/auto/walkers");
    public static final Path VARIANTS_PATH = Paths.get(DATA_PATH.toString(), "variants");
    public static final Path SKILLS_PATH = Paths.get(DATA_PATH.toString(), "skills");

    public static Path getGeneratedTypeProviderPath(ResourceLocation entityType) {
        return Paths.get(VARIANTS_PATH.toString(), entityType.getNamespace() + "_" + entityType.getPath() + ".json");
    }

    static void createDirectories() throws IOException {
        if (!Files.exists(GENERATED_PATH)) {
            Files.createDirectories(GENERATED_PATH);
        }

        if (!Files.exists(DATA_PATH)) {
            Files.createDirectories(DATA_PATH);
        }

        if (!Files.exists(VARIANTS_PATH)) {
            Files.createDirectories(VARIANTS_PATH);
        }

        if (!Files.exists(SKILLS_PATH)) {
            Files.createDirectories(SKILLS_PATH);
        }
    }
}
