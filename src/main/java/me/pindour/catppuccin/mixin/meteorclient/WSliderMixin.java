package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10 {
/*import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }

//? if >=1.21.10 && <=1.21.11
//import net.minecraft.client.input.MouseButtonEvent;

@Mixin(value = WSlider.class, remap = false)
public abstract class WSliderMixin extends WWidget {
    //? if <=1.21.10 {

    /*@Inject(
        method = "onMouseClicked",
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/input/WSlider;dragging:Z",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        )
    )
    private void catppuccin$onMouseClicked(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        ((IWidgetBackport)this).catppuccin$setFocused(true);
    }

    @Inject(
        method = "onMouseReleased",
        at = @At(
            value = "FIELD",
            target = "Lmeteordevelopment/meteorclient/gui/widgets/input/WSlider;dragging:Z",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        )
    )
    private void catppuccin$onMouseReleased(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
        ((IWidgetBackport)this).catppuccin$setFocused(false);
    }

    *///? }
}
