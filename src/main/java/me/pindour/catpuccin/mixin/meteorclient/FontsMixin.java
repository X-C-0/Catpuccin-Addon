package me.pindour.catpuccin.mixin.meteorclient;

import me.pindour.catpuccin.CatpuccinAddon;
import me.pindour.catpuccin.gui.text.RichTextRenderer;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.utils.render.FontUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static meteordevelopment.meteorclient.renderer.Fonts.FONT_FAMILIES;

@Mixin(value = Fonts.class, remap = false)
public abstract class FontsMixin {
    @Inject(method = "refresh",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;sort(Ljava/util/Comparator;)V",
            shift = At.Shift.BEFORE
        )
    )
    private static void onRefresh(CallbackInfo ci) {
        Path assetsPath = getAssetsDir();
        if (assetsPath != null) FontUtils.loadSystem(FONT_FAMILIES, new File(assetsPath + "/fonts"));
    }

    @Inject(method = "load", at = @At("HEAD"))
    private static void onLoad(FontFace fontFace, CallbackInfo ci) {
        if (!(GuiThemes.get() instanceof CatpuccinGuiTheme theme)) return;

        try {
            theme.setTextRenderer(new RichTextRenderer(fontFace));
        } catch (Exception e) {
            CatpuccinAddon.LOG.error("Failed to load font: {}", fontFace, e);
        }
    }

    @Unique
    private static @Nullable Path getAssetsDir() {
        try {
            URL resource = FontsMixin.class.getResource("/assets/" + CatpuccinAddon.MOD_ID + "/");

            if (resource == null) {
                CatpuccinAddon.LOG.warn("Could not find assets directory for {}", CatpuccinAddon.MOD_ID);
                return null;
            }

            if (!"file".equals(resource.getProtocol())) {
                CatpuccinAddon.LOG.warn("Assets directory for {} is not a file URL: {}", CatpuccinAddon.MOD_ID, resource);
                return null;
            }

            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            CatpuccinAddon.LOG.error("Failed to get assets directory for {}", CatpuccinAddon.MOD_ID, e);
            return null;
        }
    }
}
