package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Tab.class, remap = false)
public abstract class TabMixin {

    @Redirect(
        method = "openScreen",
        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/gui/tabs/TabScreen;addDirect(Lmeteordevelopment/meteorclient/gui/widgets/WWidget;)Lmeteordevelopment/meteorclient/gui/utils/Cell;"
        )
    )
    public Cell<?> catppuccin$addDirect(TabScreen screen, WWidget widget) {
        // Add a small margin at the top
        return screen.addDirect(widget).marginTop(10).top().centerX();
    }
}
