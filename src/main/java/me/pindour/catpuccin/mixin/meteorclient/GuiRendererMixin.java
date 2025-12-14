package me.pindour.catpuccin.mixin.meteorclient;

import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiRenderer.class, remap = false)
public abstract class GuiRendererMixin {
    @Unique
    private static CatpuccinRenderer catpuccinRenderer = CatpuccinRenderer.get();

    @Shadow
    private static Texture TEXTURE;

    @Shadow
    @Final
    private Renderer2D r;
    @Shadow
    @Final
    private Renderer2D rTex;

    @Shadow
    @Final
    private List<TextOperation> texts;

    @Shadow
    public GuiTheme theme;

    @Inject(method = "init", at = @At("HEAD"))
    private static void onPreInit(CallbackInfo ci) {
        CatpuccinIcons.init();
    }

    @Inject(method = "init", at = @At("TAIL"))
    private static void onPostInit(CallbackInfo ci) {
        CatpuccinRenderer.init(TEXTURE);
    }

    @Inject(method = "beginRender", at = @At("HEAD"))
    private void onBeginRender(CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;
        catpuccinRenderer.begin();
    }

    @Inject(
        method = "endRender(Lmeteordevelopment/meteorclient/gui/renderer/Scissor;)V",
        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/renderer/Renderer2D;end()V",
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onEndRender(Scissor scissor, CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;

        catpuccinRenderer.end();
        catpuccinRenderer.render();

        // From GuiRenderer
        r.render();
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());

        // From GuiRenderer
        texts.clear();

        catpuccinRenderer.renderText();

        // From GuiRenderer
        if (scissor != null) scissor.pop();
        ci.cancel();
    }

    @Inject(method = "setAlpha", at = @At("HEAD"))
    private void onSetAlpha(double a, CallbackInfo ci) {
        catpuccinRenderer.setAlpha(a);
    }

    @Inject(method = "text", at = @At("HEAD"), cancellable = true)
    private void onText(String text, double x, double y, Color color, boolean title, CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;

        catpuccinRenderer.text(RichText.of(text).boldIf(title), x, y, color);

        ci.cancel();
    }
}
