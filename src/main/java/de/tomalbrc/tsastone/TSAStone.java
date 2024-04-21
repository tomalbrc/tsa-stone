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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class TSAStone implements ModInitializer {
    private static Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        try {
            // enable whatever is needed

            registerBlocks("/filament/block");
            //registerItems("/filament/item");
            //registerDecorations("/filament/decoration");
            //registerModels("/filament/model", "mynamespace");
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
                BlockRegistry.register(new FileInputStream(f));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerItems(String path) {
        search(f -> {
            try {
                ItemRegistry.register(new FileInputStream(f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerDecorations(String path) {
        search(f -> {
            try {
                DecorationRegistry.register(new FileInputStream(f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path);
    }

    public void registerModels(String path, String namespace) {
        searchModel(f -> {
            try {
                if (f.getPath().endsWith(".bbmodel"))
                    ModelRegistry.registerAjModel(new FileInputStream(f), new ResourceLocation(namespace, f.toString()));
                else
                    ModelRegistry.registerBbModel(new FileInputStream(f), new ResourceLocation(namespace, f.toString()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, path);
    }


    public static void search(Consumer<File> registry, String path) {
        search(registry, path, ".json");
    }

    public static void searchModel(Consumer<File> registry, String path) {
        search(registry, path, ".ajmodel");
        search(registry, path, ".bbmodel");
    }

    public static void search(Consumer<File> registry, String path, String ext) {
        var r = TSAStone.class.getResource(path);
        process(r, registry, ext, path);
    }

    private static void process(URL r, Consumer<File> registry, String ext, String path) {
        final File dir = new File(r.getPath());
        var list = dir.listFiles();
        for (var item: list) {
            if (item.isDirectory()) {
                var path2 = path + "/" +  item.getName();
                process(TSAStone.class.getResource(path2), registry, ext, path2);
            } else if (item.getPath().endsWith(ext)) {
                registry.accept(item);
            }
        }
    }
}
