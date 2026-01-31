package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///? }

@Mixin(value = WDropdown.class, remap = false)
public abstract class WDropdownMixin extends WWidget {
    //? if <=1.21.10 {

    /*@Shadow protected boolean expanded;

    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/gui/renderer/GuiRenderer;absolutePost(Ljava/lang/Runnable;)V"
        )
    )
    private boolean catppuccin$render(GuiRenderer renderer, Runnable task) {
        WView view = ((IWidgetBackport) this).catppuccin$getView();
        WWidget root = getDropdownRoot();

        boolean shouldRender = true;

        if (view != null && root != null) {
            shouldRender = ((IWidgetBackport)view).catppuccin$isWidgetInView(root);
        }

        return shouldRender;
    }

    @Inject(method = "onPressed", at = @At("TAIL"))
    private void catppuccin$onPressed(int button, CallbackInfo ci) {
        WWidget root = getDropdownRoot();

        if (root != null) ((IWidgetBackport) root).catppuccin$setFocused(expanded);

        ((IWidgetBackport) this).catppuccin$setFocused(expanded);
    }

    @Unique
    private WWidget getDropdownRoot() {
        try {
            // Bruh
            java.lang.reflect.Field rootField = WDropdown.class.getDeclaredField("root");
            rootField.setAccessible(true);
            Object rootObj = rootField.get(this);

            if (rootObj instanceof WWidget)
                return (WWidget) rootObj;

            return null;

        } catch (Exception ignored) { }

        return null;
    }

    *///? }
}
