package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.api.icons.CatppuccinIcons;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screens.Screen;

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
        private final GuiTexture icon;

        private Animation selectedAnimation;

        public WTopBarButton(Tab tab) {
            this.tab = tab;
            text = RichText.of(tab.name);
            icon = CatppuccinIcons.getTabIcon(tab.getClass());
        }

        @Override
        public void init() {
            selectedAnimation = new Animation(Easing.QUART_OUT, 300);
        }

        private boolean hasIcon() {
            return icon != null && theme().tabIcons.get();
        }

        private double iconSize() {
            return theme.textHeight();
        }

        @Override
        protected void onCalculateSize() {
            double pad = theme.scale(8);

            double iconWidth = hasIcon() ? iconSize() + theme.scale(4) : 0;

            width = pad + iconWidth + theme().textWidth(text) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onPressed(int button) {
            Screen screen = mc.gui.screen();

            if (!(screen instanceof TabScreen) || ((TabScreen) screen).tab != tab) {
                double mouseX = mc.mouseHandler.xpos();
                double mouseY = mc.mouseHandler.ypos();

                tab.openScreen(theme);

                glfwSetCursorPos(
                        //? if <=1.21.4 {
                        /*mc.getWindow().getWindow(),
                        *///? } else
                        mc.getWindow().handle(),
                        mouseX,
                        mouseY
                );
            }
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();

            boolean isSelected = mc.gui.screen() instanceof TabScreen && ((TabScreen) mc.gui.screen()).tab == tab;

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

            Color color = isSelected ? theme.accentColor() : theme.textColor();

            double textWidth = theme.textWidth(text);
            double gap = theme.scale(4);
            double contentWidth = textWidth + (hasIcon() ? iconSize() + gap : 0);

            double offsetX = width / 2 - contentWidth / 2;
            double offsetY = height / 2 - theme.textHeight() / 2;

            if (hasIcon()) {
                double iconY = height / 2 - iconSize() / 2;

                renderer.quad(x + offsetX, y + iconY, iconSize(), iconSize(), icon, color);

                offsetX += iconSize() + gap;
            }

            renderer().text(
                    text,
                    x + offsetX,
                    y + offsetY,
                    color
            );
        }
    }
}
