package me.pindour.catpuccin.mixin.meteorclient;

import me.pindour.catpuccin.mixininterface.IWContainer;
import me.pindour.catpuccin.utils.WidgetUtils;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WContainer.class, remap = false)
public abstract class WContainerMixin extends WWidget implements IWContainer {

    @Unique
    WView parentView;

    @Inject(method = "propagateEvents", at = @At("HEAD"), cancellable = true)
    private void onPropagateEvents(WWidget widget, CallbackInfoReturnable<Boolean> cir) {
        // There's probably a better way to do this, but initializing parent view on widget init is too early so deal with it
        if (parentView == null) parentView = WidgetUtils.findParentView(this);
        if (parentView != null) cir.setReturnValue(WidgetUtils.isWidgetInside(widget, parentView));
    }

    @Override
    public WView catpuccin$getParentView() {
        return parentView;
    }
}
