package me.pindour.catpuccin.mixin.meteorclient;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "meteordevelopment.meteorclient.gui.widgets.containers.WWindow$WHeader", remap = false)
public abstract class WHeaderMixin {

    @Shadow @Final
    WWindow this$0;

    @Redirect(method = "render", at = @At(
                    value = "FIELD",
                    target = "Lmeteordevelopment/meteorclient/gui/widgets/pressable/WTriangle;rotation:D",
                    opcode = Opcodes.PUTFIELD))

    private void onRender(WTriangle triangle, double value) {
        if (!(this$0.theme instanceof CatpuccinGuiTheme)) {
            // Ignore triangle rotation if we are using our theme (idiot null triangul)
            triangle.rotation = value;
        }
    }
}
