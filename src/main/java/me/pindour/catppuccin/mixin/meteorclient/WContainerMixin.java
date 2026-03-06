package me.pindour.catppuccin.mixin.meteorclient;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import me.pindour.catppuccin.utils.ScissorAnimOffset;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.utils.Utils;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
//? if <=1.21.10 {
/*import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static meteordevelopment.meteorclient.utils.Utils.getWindowHeight;
*///? }

@Mixin(value = WContainer.class, remap = false)
public abstract class WContainerMixin extends WWidget implements IWidgetBackport {
    //? if <=1.21.10 {

    /*@Final @Shadow public List<Cell<?>> cells;

    @Shadow protected abstract void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void catppuccin$render(GuiRenderer renderer, double mouseX, double mouseY, double delta, CallbackInfoReturnable<Boolean> cir) {
        if (super.render(renderer, mouseX, mouseY, delta)) {
            cir.setReturnValue(true);
            return;
        }

        WView view = catppuccin$getView();
        double windowHeight = getWindowHeight();

        for (Cell<?> cell : cells) {
            WWidget widget = cell.widget();

            if (widget.y > windowHeight) break;
            if (widget.y + widget.height <= 0) continue;

            if (shouldRenderWidget(widget, view)) renderWidget(widget, renderer, mouseX, mouseY, delta);
        }

        cir.setReturnValue(false);
    }

    @Override
    public boolean catppuccin$isFocused() {
        if (catppuccin$isSelfFocused()) return true;

        for (Cell<?> cell : cells) {
            if (((IWidgetBackport) cell.widget()).catppuccin$isFocused())
                return true;
        }

        return false;
    }

    @Unique
    private boolean shouldRenderWidget(WWidget widget, WView view) {
        if (view == null) return true;
        if (!((IWidgetBackport)view).catppuccin$isWidgetInView(widget)) return false;

        if (widget.mouseOver && !view.mouseOver) {
            widget.mouseOver = false;
        }

        return true;
    }

    *///? }

    //? if >=1.21.11 {
    @Shadow
    @Final
    public List<Cell<?>> cells;

    @Shadow protected abstract void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta);

    @Unique
    private Animation animation;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void catppuccin$render(GuiRenderer renderer, double mouseX, double mouseY, double delta, CallbackInfoReturnable<Boolean> cir) {
        if (!getClass().getSimpleName().equals("WFullScreenRoot")) return;
        if (!(mc.currentScreen instanceof TabScreen)) return;
        if (cells.isEmpty()) return;

        if (animation == null) {
            animation = new Animation(Easing.LINEAR, 1000);
            animation.start();
        }

        if (animation.isFinished()) return;

        double offsetY = (-(Utils.getWindowHeight() * (1 - animation.getProgress()))) / mc.getWindow().getScaleFactor();

        GuiRendererAccessor rendererAccessor = (GuiRendererAccessor) renderer;
        Matrix4fStack mvStack = RenderSystem.getModelViewStack();

        // Flush current batch before changing matrix
//        rendererAccessor.catppuccin$endRender(null);
        mvStack.pushMatrix();
        mvStack.translate(0f, (float) offsetY, 0f);
        ScissorAnimOffset.active = true;
        ScissorAnimOffset.offsetY = offsetY;
//        rendererAccessor.catppuccin$beginRender();

        int topBarIndex = -1;

        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).widget() instanceof WTopBar) {
                topBarIndex = i;
                continue;
            }

            renderWidget(cells.get(i).widget(), renderer, mouseX, mouseY, delta);
        }

        // Flush translated batch, restore matrix
//        rendererAccessor.catppuccin$endRender(null);
        mvStack.popMatrix();
        ScissorAnimOffset.active = false;
//        rendererAccessor.catppuccin$beginRender();

        // Render top bar without animation
        if (topBarIndex != -1) {
            renderWidget(cells.get(topBarIndex).widget(), renderer, mouseX, mouseY, delta);
        }

        cir.setReturnValue(false);
    }
    //? }
}
