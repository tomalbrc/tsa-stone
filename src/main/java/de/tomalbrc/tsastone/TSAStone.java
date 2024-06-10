package de.tomalbrc.tsastone;

import com.mojang.logging.LogUtils;
import de.tomalbrc.filament.registry.filament.BlockRegistry;
import de.tomalbrc.filament.registry.filament.DecorationRegistry;
import de.tomalbrc.filament.registry.filament.ItemRegistry;
import de.tomalbrc.filament.registry.filament.ModelRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TSAStone implements ModInitializer {
    private static Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        try {
            // enable whatever is needed

            registerBlocks("filament/block");
            //registerItems("filament/item");
            //registerDecorations("filament/decoration");
            //registerModels("filament/model", "mynamespace");
        } catch (Exception e) {
            LOGGER.error("Could not load some files!");
            e.printStackTrace();
        }

        PolymerResourcePackUtils.addModAssets("tsa-stone");
        PolymerResourcePackUtils.markAsRequired();
    }
    public void registerBlocks(String path) {
        search(f -> {
            try {
                BlockRegistry.register(f);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerItems(String path) {
        search(f -> {
            try {
                ItemRegistry.register(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerDecorations(String path) {
        search(f -> {
            try {
                DecorationRegistry.register(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerModels(String path, String namespace) {
        search(f -> {
            try {
                ModelRegistry.registerAjModel(f, new ResourceLocation(namespace, f.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path, ".ajmodel");

        search(f -> {
            try {
                ModelRegistry.registerBbModel(f, new ResourceLocation(namespace, f.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path, ".bbmodel");

    }

    public static void search(Consumer<InputStream> registry, String path) {
        search(registry, path, ".json");
    }

    public static void search(Consumer<InputStream> registry, String path, String ext) {
        processJsonFilesInJar(registry, path, ext);
    }

    public static void processJsonFilesInJar(Consumer<InputStream> consumer, String rootPath, String ext) {
        String jarPath = TSAStone.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.endsWith("jar")) {
            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String fileName = entry.getName();
                    if (!entry.isDirectory() && fileName.startsWith(rootPath) && fileName.endsWith(ext)) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            consumer.accept(inputStream);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Not a jar");
        }
    }
}
