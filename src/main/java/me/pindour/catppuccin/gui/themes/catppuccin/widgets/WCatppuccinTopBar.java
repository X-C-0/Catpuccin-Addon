package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.utils.ColorUtils;
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
            add(new WTopBarButton(tab)).pad(theme.scale(5));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        roundedRect().bounds(this)
                     .radius(radius())
                     .color(theme().baseColor())
                     .render();
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
        private final RichText text;

        private Animation selectedAnimation;

        public WTopBarButton(Tab tab) {
            this.tab = tab;
            text = RichText.of(tab.name);
        }

        @Override
        public void init() {
            selectedAnimation = new Animation(Easing.LINEAR, 300);
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
            CatppuccinGuiTheme theme = theme();

            boolean isSelected = mc.currentScreen instanceof TabScreen && ((TabScreen) mc.currentScreen).tab == tab;

            // Start the animation if it wasn't started yet, selecting a new tab
            // will automatically reset the animation, since it gets reinitialized
            if (isSelected && !selectedAnimation.isRunning() && !selectedAnimation.isFinished())
                selectedAnimation.start();

            double selectedProgress = selectedAnimation.getProgress();

            // Hover
            if (mouseOver && selectedProgress == 0) {
                roundedRect().bounds(this)
                             .radius(smallRadius())
                             .color(theme.surface0Color())
                             .render();
            }

            // Selected highlight
            if (selectedProgress > 0) {
                // Outer glow
                double glowSize = theme.scale(2);
                roundedRect().pos(x - glowSize, y - glowSize)
                             .size(width + glowSize * 2, height + glowSize * 2)
                             .radius(smallRadius() + glowSize)
                             .color(ColorUtils.withAlpha(theme.accentColor(), (int)(30 * selectedProgress)))
                             .render();

                // Main highlight
                roundedRect().bounds(this)
                             .radius(smallRadius())
                             .color(ColorUtils.withAlpha(theme.accentColor(), 60))
                             .render();
            }

            double offsetX = width / 2 - theme.textWidth(text) / 2;
            double offsetY = height / 2 - theme.textHeight() / 2;

            renderer().text(
                    text,
                    x + offsetX,
                    y + offsetY,
                    isSelected ? theme.accentColor() : theme.textColor()
            );
        }
    }
}
