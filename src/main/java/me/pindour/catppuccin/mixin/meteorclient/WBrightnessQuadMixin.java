package me.pindour.catppuccin.mixin.meteorclient;

import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

@Mixin(targets = "meteordevelopment.meteorclient.gui.screens.settings.ColorSettingScreen$WBrightnessQuad", remap = false)
public abstract class WBrightnessQuadMixin {
    //? if <=1.21.10 {

    /*@Inject(
            method = "onMouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lmeteordevelopment/meteorclient/gui/screens/settings/ColorSettingScreen$WBrightnessQuad;dragging:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void catppuccin$onMouseClicked(Click click, boolean used, CallbackInfoReturnable<Boolean> cir) {
        ((IWidgetBackport)this).catppuccin$setFocused(true);
    }

    @Inject(
            method = "onMouseReleased",
            at = @At(
                    value = "FIELD",
                    target = "Lmeteordevelopment/meteorclient/gui/screens/settings/ColorSettingScreen$WBrightnessQuad;dragging:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void catppuccin$onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        ((IWidgetBackport)this).catppuccin$setFocused(false);
    }

    *///? }
}
