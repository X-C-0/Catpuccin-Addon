package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
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

public class WCatpuccinTopBar extends WTopBar implements CatpuccinWidget {

    @Override
    public void init() {
        for (Tab tab : Tabs.get())
            add(new WTopBarButton(tab));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        catpuccinRenderer().roundedRect(this, cornerRadius(), theme().baseColor(), CornerStyle.ALL);
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
            double pad = theme.scale(12);
            double s = theme.scale(4);
            boolean isSelected = mc.currentScreen instanceof TabScreen && ((TabScreen) mc.currentScreen).tab == tab;

            if (isSelected)
                catpuccinRenderer().roundedRect(
                        x + s, y + s,
                        width - s * 2, height - s * 2,
                        smallCornerRadius(), theme().accentColor(), CornerStyle.ALL
                );

            RichText text = RichText.of(tab.name);
            double offset = width / 2 - theme().textWidth(text) / 2;

            catpuccinRenderer().text(
                    text,
                    x + offset,
                    y + pad,
                    isSelected ? theme().baseColor() : theme().textColor()
            );
        }
    }
}
