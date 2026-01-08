package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinIcons;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//? if <=1.21.4 {
/*import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

*///?} else {
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import meteordevelopment.meteorclient.renderer.Texture;
//?}

@Mixin(value = GuiRenderer.class, remap = false)
public abstract class GuiRendererMixin {
    @Unique
    private static CatppuccinRenderer catppuccinRenderer = CatppuccinRenderer.get();

    //? if <=1.21.4 {
    /*@Shadow
    private DrawContext drawContext;

    @Shadow
    private static ByteTexture TEXTURE;

    *///?} else {
    @Shadow
    private static Texture TEXTURE;
    //?}

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
        CatppuccinIcons.init();
    }

    //? if >=1.21.5 {
    @Inject(method = "init", at = @At("TAIL"))
    private static void onPostInit(CallbackInfo ci) {
        CatppuccinRenderer.init(TEXTURE);
    }
    //?}

    @Inject(method = "beginRender", at = @At("HEAD"))
    private void onBeginRender(CallbackInfo ci) {
        if (!(theme instanceof CatppuccinGuiTheme)) return;
        catppuccinRenderer.begin();
    }

    @Inject(
        //? if <=1.21.4
        //method = "endRender()V",
        //? if >=1.21.5
        method = "endRender(Lmeteordevelopment/meteorclient/gui/renderer/Scissor;)V",

        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/renderer/Renderer2D;end()V",
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onEndRender(
            //? if >=1.21.5
            Scissor scissor,
            CallbackInfo ci
    ) {
        if (!(theme instanceof CatppuccinGuiTheme)) return;

        //? if <=1.21.4 {
        /*MatrixStack matrices = drawContext.getMatrices();

        catppuccinRenderer.end();
        catppuccinRenderer.render(matrices);

        GL.bindTexture(TEXTURE.getGlId());
        catppuccinRenderer.renderTexture(matrices);

        r.render(matrices);
        rTex.render(matrices);

        *///?} else {
        catppuccinRenderer.end();
        catppuccinRenderer.render();

        r.render();
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());
        //?}

        // From GuiRenderer
        texts.clear();

        catppuccinRenderer.renderText();

        // From GuiRenderer
        //? if >=1.21.5
        if (scissor != null) scissor.pop(); 
        ci.cancel();
    }

    @Inject(method = "setAlpha", at = @At("HEAD"))
    private void onSetAlpha(double a, CallbackInfo ci) {
        catppuccinRenderer.setAlpha(a);
    }

    @Inject(method = "text", at = @At("HEAD"), cancellable = true)
    private void onText(String text, double x, double y, Color color, boolean title, CallbackInfo ci) {
        if (!(theme instanceof CatppuccinGuiTheme)) return;

        catppuccinRenderer.text(RichText.of(text).boldIf(title), x, y, color);

        ci.cancel();
    }
}
