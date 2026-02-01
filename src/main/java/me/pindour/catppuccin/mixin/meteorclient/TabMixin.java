package me.pindour.catppuccin.mixin.meteorclient;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Tab.class, remap = false)
public abstract class TabMixin {

    @WrapOperation(
        method = "openScreen",
        at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/gui/tabs/TabScreen;addDirect(Lmeteordevelopment/meteorclient/gui/widgets/WWidget;)Lmeteordevelopment/meteorclient/gui/utils/Cell;"
        )
    )
    private Cell<?> catppuccin$addTopBarMargin(TabScreen screen, WWidget widget, Operation<Cell<?>> original) {
        Cell<?> cell = original.call(screen, widget);

        // Add a small margin at the top, but only when our theme is active
        if (GuiThemes.get() instanceof CatppuccinGuiTheme)
            cell.marginTop(10);

        return cell;
    }
}
