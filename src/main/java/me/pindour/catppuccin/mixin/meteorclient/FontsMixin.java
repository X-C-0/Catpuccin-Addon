package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.CatppuccinAddon;
import me.pindour.catppuccin.renderer.text.RichTextRenderer;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Fonts.class, remap = false)
public abstract class FontsMixin {

    @Inject(method = "load", at = @At("HEAD"))
    private static void catppuccin$load(FontFace fontFace, CallbackInfo ci) {
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme theme)) return;

        if (theme.textRenderer() instanceof RichTextRenderer currentRenderer)
             if (currentRenderer.fontFace.equals(fontFace)) return;

        try {
            theme.setTextRenderer(new RichTextRenderer(fontFace));
        } catch (Exception e) {
            CatppuccinAddon.LOG.error("Failed to load font: {}", fontFace, e);
        }
    }
}
