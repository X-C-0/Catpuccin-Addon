package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
*///? }

//? if <1.21.10 {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

@Mixin(value = WTextBox.class, remap = false)
public abstract class WTextBoxMixin extends WWidget {
    //? if <=1.21.10 {

    /*@Redirect(
        method = {
                "render",
                "onMouseClicked",
                "onKeyPressed",
                "onKeyRepeated",
                "onCharTyped",
                "setFocused",
                //? if <1.21.10
                //"isFocused"
        },
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/input/WTextBox;focused:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean catppuccin$getFocused(WTextBox instance) {
        return ((IWidgetBackport) instance).catppuccin$isSelfFocused();
    }

    @Redirect(
        method = { "setFocused" },
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/input/WTextBox;focused:Z",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void catppuccin$setFocused(WTextBox instance, boolean value) {
        ((IWidgetBackport) instance).catppuccin$setFocused(value);
    }

    // isFocused got removed for some reason?
    //? if <1.21.10 {
    /^@Inject(method = "isFocused", at = @At("HEAD"), cancellable = true)
    public void catppuccin$onIsFocused(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((IWidgetBackport) this).catppuccin$isFocused());
    }
    ^///? }

    *///? }
}