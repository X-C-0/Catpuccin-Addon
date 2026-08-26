package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.icons.CatppuccinIcons;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.tabs.TabBridge;
import me.pindour.catppuccin.gui.widgets.tabs.WTabView;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

/**
 * Singleton-like top bar widget. Kept persistent across screen switches
 * to preserve tab indicator cool transition animations.
 */
public class WCatppuccinTopBar extends WTopBar implements CatppuccinWidget {
    private boolean invalid = true;
    private WTabView tabView;

    private TabBridge tabBridge;
    private Tab pendingTab;

    @Override
    public void init() {
        if (invalid) {
            tabBridge = new TabBridge(
                    Tabs.get(),
                    theme().tabIcons.get()
                            ? tab -> CatppuccinIcons.getTabIcon(tab.getClass())
                            : null
            );
            clear();

            tabView = add(theme().tabView(
                    tabBridge.catppuccinTabs(),
                    tabBridge.catppuccinTab(pendingTab)
            )).widget();

            tabView.onTabChange = tab -> open(tabBridge.meteorTab(tab));

            invalid = false;
        }

        pendingTab = null;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        roundedRect().bounds(this)
                     .radius(radius())
                     .color(theme().baseColor())
                     .shadow(shadow())
                     .render();
    }

    public void onTabScreenOpen(Tab tab) {
        invalid = tabView == null || !tabView.isTabActive(tabBridge.catppuccinTab(tab));
        pendingTab = tab;
    }

    private void open(Tab tab) {
        if (tab == null) return;

        double mouseX = mc.mouseHandler.xpos();
        double mouseY = mc.mouseHandler.ypos();

        tab.openScreen(theme());

        glfwSetCursorPos(
                //? if <=1.21.4 {
                /*mc.getWindow().getWindow(),
                *///? } else {
                mc.getWindow().handle(),
                //? }
                mouseX,
                mouseY
        );
    }

    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor();
    }
}
