package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

@Mixin(value = WView.class, remap = false)
public abstract class WViewMixin extends WContainer implements IWidgetBackport {
    //? if <=1.21.10 {

    /*@Inject(method = "propagateEvents", at = @At("HEAD"), cancellable = true)
    protected void catppuccin$propagateEvents(WWidget widget, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((mouseOver && catppuccin$isWidgetInView(widget)) || ((IWidgetBackport)widget).catppuccin$isFocused());
    }

    @Override
    public boolean catppuccin$isWidgetInView(WWidget widget) {
        return widget.y < y + height && widget.y + widget.height > y;
    }

    *///? }
}
