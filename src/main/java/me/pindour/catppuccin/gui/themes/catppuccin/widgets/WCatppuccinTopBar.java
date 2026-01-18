package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.renderer.CornerStyle;
import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

public class WCatppuccinTopBar extends WTopBar implements CatppuccinWidget {

    @Override
    public void init() {
        for (Tab tab : Tabs.get())
            add(new WTopBarButton(tab));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer().roundedRect(this, radius(), theme().baseColor(), CornerStyle.ALL);
    }

    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor();
    }

    protected class WTopBarButton extends WPressable {
        private final Tab tab;

        public WTopBarButton(Tab tab) {
            this.tab = tab;
        }

        @Override
        protected void onCalculateSize() {
            double pad = theme.scale(12);

            width = pad + theme().textWidth(RichText.bold(tab.name)) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onPressed(int button) {
            Screen screen = mc.currentScreen;

            if (!(screen instanceof TabScreen) || ((TabScreen) screen).tab != tab) {
                double mouseX = mc.mouse.getX();
                double mouseY = mc.mouse.getY();

                tab.openScreen(theme);
                glfwSetCursorPos(mc.getWindow().getHandle(), mouseX, mouseY);
            }
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();
            double pad = theme.scale(4);
            boolean isSelected = mc.currentScreen instanceof TabScreen && ((TabScreen) mc.currentScreen).tab == tab;

            if (isSelected || mouseOver) {
                Color color = isSelected ? theme.accentColor() : theme.surface0Color();

                renderer().roundedRect(
                        x + pad, y + pad,
                        width - pad * 2, height - pad * 2,
                        radius() - pad, color, CornerStyle.ALL
                );
            }

            RichText text = RichText.of(tab.name);
            double offsetX = width / 2 - theme.textWidth(text) / 2;
            double offsetY = height / 2 - theme.textHeight() / 2;

            renderer().text(
                    text,
                    x + offsetX,
                    y + offsetY,
                    isSelected ? theme.baseColor() : theme.textSecondaryColor()
            );
        }
    }
}
