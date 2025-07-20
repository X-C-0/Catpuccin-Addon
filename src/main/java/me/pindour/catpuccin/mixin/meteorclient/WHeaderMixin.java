package me.pindour.catpuccin.mixin.meteorclient;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.containers.WWindow$WHeader", remap = false)
public abstract class WHeaderMixin {
    @Shadow
    @Final
    WWindow this$0;

    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lmeteordevelopment/meteorclient/gui/widgets/pressable/WTriangle;rotation:D",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD
            )
    )
    private boolean shouldSetRotation(WTriangle triangle, double value) {
        // Only allow rotation assignment if we're not using our theme
        return !(this$0.theme instanceof CatpuccinGuiTheme);
    }
}