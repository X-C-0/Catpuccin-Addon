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
        Tab firstTab = Tabs.get().getFirst();
        Tab lastTab = Tabs.get().getLast();

        for (Tab tab : Tabs.get())
            add(new WTopBarButton(tab, firstTab.equals(tab), lastTab.equals(tab)));
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
        private final boolean isFirst;
        private final boolean isLast;

        public WTopBarButton(Tab tab, boolean isFirst, boolean isLast) {
            this.tab = tab;
            this.isFirst = isFirst;
            this.isLast = isLast;
        }

        @Override
        protected void onCalculateSize() {
            double pad = theme.scale(8);

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
            double pad = theme.scale(8);
            boolean isSelected = mc.currentScreen instanceof TabScreen && ((TabScreen) mc.currentScreen).tab == tab;

            Color color = getButtonColor(pressed || isSelected, mouseOver);

            if (isFirst)
                catpuccinRenderer().roundedRect(this, cornerRadius, color, CornerStyle.BOTTOM_LEFT);

            else if (isLast)
                catpuccinRenderer().roundedRect(this, cornerRadius, color, CornerStyle.BOTTOM_RIGHT);

            else renderer.quad(x, y, width, height, color);

            RichText text = RichText.of(tab.name).boldIf(isSelected);
            double offset = width / 2 - theme().textWidth(text) / 2;

            catpuccinRenderer().text(
                    text,
                    x + offset,
                    y + pad,
                    isSelected ? theme().accentColor() : theme().textColor()
            );
        }
    }
}
