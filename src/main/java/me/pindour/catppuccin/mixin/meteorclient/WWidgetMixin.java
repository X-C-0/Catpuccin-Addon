package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

@Mixin(value = WWidget.class, remap = false)
public abstract class WWidgetMixin implements IWidgetBackport {
    //? if <=1.21.10 {

    /*@Shadow public WWidget parent;
    @Shadow public String tooltip;

    @Shadow protected double mouseOverTimer;
    @Shadow public boolean visible;

    @Shadow public abstract boolean isOver(double x, double y);
    @Shadow protected abstract void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta);

    @Unique
    public boolean focused;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void catppuccin$render(GuiRenderer renderer, double mouseX, double mouseY, double delta, CallbackInfoReturnable<Boolean> cir) {
        if (!visible) cir.setReturnValue(true);

        if (isOver(mouseX, mouseY)) {
            mouseOverTimer += delta;

            if ((mouseOverTimer >= 1) && tooltip != null) {
                WView view = catppuccin$getView();
                if (view == null || view.mouseOver) renderer.tooltip(tooltip);
            }
        } else {
            mouseOverTimer = 0;
        }

        onRender(renderer, mouseX, mouseY, delta);
        cir.setReturnValue(false);
    }

    @Override
    public WView catppuccin$getView() {
        return (Object) this instanceof WView ? (WView) (Object) this : (parent != null ? ((IWidgetBackport) parent).catppuccin$getView() : null);
    }

    @Override
    public boolean catppuccin$isFocused() {
        return focused;
    }

    @Override
    public void catppuccin$setFocused(boolean focused) {
        if (this.focused != focused) this.focused = focused;
    }

    *///? }
}
