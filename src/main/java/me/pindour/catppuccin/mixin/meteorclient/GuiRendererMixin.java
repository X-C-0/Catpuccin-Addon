package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.gui.renderer.Scissor;
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

//? if <=1.21.9 {
/*import java.util.Stack;
*///?} else {
import it.unimi.dsi.fastutil.Stack;
//?}

//? if <=1.21.4 {
/*import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
*///?} else {
import meteordevelopment.meteorclient.renderer.Texture;
//?}

@Mixin(value = GuiRenderer.class, remap = false)
public abstract class GuiRendererMixin {
    //? if <=1.21.4 {
    /*@Shadow private DrawContext drawContext;
    @Shadow private static ByteTexture TEXTURE;
    *///?} else {
    @Shadow private static Texture TEXTURE;
    //?}

    @Shadow @Final private Renderer2D r;
    @Shadow @Final private Renderer2D rTex;
    @Shadow @Final private List<TextOperation> texts;
    @Shadow public GuiTheme theme;

    @Inject(method = "init", at = @At("HEAD"))
    private static void catppuccin$init(CallbackInfo ci) {
        CatppuccinBuiltinIcons.init();
    }

    @Inject(method = "beginRender", at = @At("HEAD"))
    private void catppuccin$beginRender(CallbackInfo ci) {
        if (!isCatppuccinActive()) return;
        renderer().begin();
    }

    @Inject(
            //? if <=1.21.4
            //method = "endRender()V",
            //? if >=1.21.5
            method = "endRender(Lmeteordevelopment/meteorclient/gui/renderer/Scissor;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void catppuccin$endRender(
            //? if >=1.21.5
            Scissor scissor,
            CallbackInfo ci
    ) {
        if (!isCatppuccinActive()) return;

        //? if >=1.21.5
        if (scissor != null) scissor.push();

        r.end();
        rTex.end();

        render();

        texts.clear();
        renderer().renderText();

        //? if >=1.21.5
        if (scissor != null) scissor.pop();

        ci.cancel();
    }

    @Inject(method = "text", at = @At("HEAD"), cancellable = true)
    private void catppuccin$text(String text, double x, double y, Color color, boolean title, CallbackInfo ci) {
        if (!isCatppuccinActive()) return;

        renderer().text(RichText.of(text).boldIf(title), x, y, color);
        ci.cancel();
    }

    @Inject(method = "scissorStart", at = @At("TAIL"))
    private void catppuccin$scissorStart(double x, double y, double width, double height, CallbackInfo ci) {
        if (!isCatppuccinActive()) return;
        updateClipFromStack();
    }

    @Inject(method = "scissorEnd", at = @At("TAIL"))
    private void catppuccin$scissorEnd(CallbackInfo ci) {
        if (!isCatppuccinActive()) return;
        updateClipFromStack();
    }

    // ----------------- Helper methods

    @Unique
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isCatppuccinActive() {
        return theme instanceof CatppuccinGuiTheme;
    }

    @Unique
    private CatppuccinRenderer renderer() {
        return CatppuccinRenderer.get();
    }

    @Unique
    private void render() {
        CatppuccinRenderer renderer = renderer();

        //? if <=1.21.4 {
        /*MatrixStack matrices = drawContext.getMatrices();

        renderer.end();
        renderer.render(matrices);
        r.render(matrices);
        GL.bindTexture(TEXTURE.getGlId());
        rTex.render(matrices);
        *///?} else {

        renderer.end();
        r.render();
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());
        //?}
    }

    @Unique
    private void updateClipFromStack() {
        Stack<Scissor> stack = ((GuiRendererAccessor) this).catppuccin$getScissorStack();
        if (stack == null || stack.isEmpty()) {
            renderer().clearClipRect();
            return;
        }

        Scissor top = peekScissor(stack);
        renderer().setClipRect(top.x, top.y, top.x + top.width, top.y + top.height);
    }

    @Unique
    private static Scissor peekScissor(Stack<Scissor> stack) {
        //? if <=1.21.9
        //return stack.peek();
        //? if >=1.21.10
        return stack.top();
    }
}
