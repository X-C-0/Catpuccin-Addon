package me.pindour.catpuccin.mixin.meteorclient;

import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
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

#if MC_VER < MC_1_21_10
import java.util.Stack;
#else
import it.unimi.dsi.fastutil.Stack;
#endif

#if MC_VER <= MC_1_21_4
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
#else
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import meteordevelopment.meteorclient.renderer.Texture;
#endif

@Mixin(value = GuiRenderer.class, remap = false)
public abstract class GuiRendererMixin {
    @Unique
    private static CatpuccinRenderer catpuccinRenderer = CatpuccinRenderer.get();

    #if MC_VER <= MC_1_21_4
    @Shadow
    private DrawContext drawContext;

    @Shadow
    private static ByteTexture TEXTURE;
    #else
    @Shadow
    private static Texture TEXTURE;
    #endif

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

    #if MC_VER >= MC_1_21_5
    @Inject(method = "init", at = @At("TAIL"))
    private static void onPostInit(CallbackInfo ci) {
        CatpuccinRenderer.init(TEXTURE);
    }
    #endif

    @Inject(method = "beginRender", at = @At("HEAD"))
    private void onBeginRender(CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;
        catpuccinRenderer.begin();
    }

    #if MC_VER <= MC_1_21_4
    @Inject(
        method = "endRender()V",
        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/renderer/Renderer2D;end()V",
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onEndRender(CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;

        MatrixStack matrices = drawContext.getMatrices();

        catpuccinRenderer.end();
        catpuccinRenderer.render(matrices);

        GL.bindTexture(TEXTURE.getGlId());
        catpuccinRenderer.renderTexture(matrices);

        r.render(matrices);
        rTex.render(matrices);
    #else
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

        r.render();
        renderTexture();
    #endif

        // From GuiRenderer
        texts.clear();

        catpuccinRenderer.renderText();

        // From GuiRenderer
        #if MC_VER >= MC_1_21_5
        if (scissor != null) scissor.pop();
        #endif
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

    @Inject(method = "scissorStart", at = @At("TAIL"))
    private void onScissorStart(double x, double y, double width, double height, CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;
        updateClipFromStack();
    }

    @Inject(method = "scissorEnd", at = @At("TAIL"))
    private void onScissorEnd(CallbackInfo ci) {
        if (!(theme instanceof CatpuccinGuiTheme)) return;
        updateClipFromStack();
    }

    #if MC_VER >= MC_1_21_5
    private void renderTexture() {
        #if MC_VER >= MC_1_21_11
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());
        #elif MC_VER >= MC_1_21_6
        rTex.render(TEXTURE.getGlTextureView());
        #else
        rTex.render(TEXTURE.getGlTexture());
        #endif
    }
    #endif

    private static Scissor peekScissor(Stack<Scissor> stack) {
        #if MC_VER < MC_1_21_10
        return stack.peek();
        #else
        return stack.top();
        #endif
    }

    private void updateClipFromStack() {
        Stack<Scissor> stack = ((GuiRendererAccessor) this).getScissorStack();
        if (stack == null || stack.isEmpty()) {
            catpuccinRenderer.clearClipRect();
            return;
        }

        Scissor top = peekScissor(stack);
        catpuccinRenderer.setClipRect(top.x, top.y, top.x + top.width, top.y + top.height);
    }
}
