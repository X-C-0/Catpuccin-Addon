package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

@Mixin(value = WView.class, remap = false)
public abstract class WViewMixin extends WContainer implements IWidgetBackport {
    //? if <=1.21.10 {

    /*@Shadow
    public boolean scrollOnlyWhenMouseOver;
    @Shadow
    private double actualHeight;
    @Shadow
    private double targetScroll;

    @Redirect(
        method = { "onMouseClicked", "onMouseReleased", "onMouseMoved" },
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/containers/WView;handlePressed:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean catppuccin$getHandlePressed(WView instance) {
        return ((IWidgetBackport) instance).catppuccin$isSelfFocused();
    }

    @Redirect(
        method = { "onMouseClicked", "onMouseReleased" },
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/containers/WView;handlePressed:Z",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void catppuccin$setHandlePressed(WView instance, boolean focused) {
        ((IWidgetBackport) instance).catppuccin$setFocused(focused);
    }

    @Inject(method = "propagateEvents", at = @At("HEAD"), cancellable = true)
    protected void catppuccin$propagateEvents(WWidget widget, CallbackInfoReturnable<Boolean> cir) {
        if (((IWidgetBackport)widget).catppuccin$isFocused()) {
            cir.setReturnValue(true);
            return;
        }

        // Propagate to any visible view, to allow inputs even when not hovered
        if (widget instanceof WView) {
            cir.setReturnValue(catppuccin$isWidgetInView(widget));
            return;
        }

        // Propagate to any visible widget while the view is hovered
        cir.setReturnValue(mouseOver && catppuccin$isWidgetInView(widget));
    }

    @Inject(method = "onMouseScrolled", at = @At("HEAD"), cancellable = true)
    private void catppuccin$onMouseScrolled(double amount, CallbackInfoReturnable<Boolean> cir) {
        if (!scrollOnlyWhenMouseOver || mouseOver) {
            double max = actualHeight - height;

            if (max < 0) {
                cir.setReturnValue(false);
                return;
            }

            targetScroll -= Math.round(theme.scale(amount * 40));
            targetScroll = Math.clamp(targetScroll, 0, max);

            // Only consume the event if the view actually scrolled, otherwise propagate to parent.
            cir.setReturnValue(targetScroll > 0 && targetScroll < max);
            return;
        }

        cir.setReturnValue(false);
    }

    @Override
    public boolean catppuccin$isWidgetInView(WWidget widget) {
        return widget.y < y + height && widget.y + widget.height > y;
    }

    *///? }
}
