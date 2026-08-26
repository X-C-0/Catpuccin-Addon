package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinButton;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.function.Consumer;
import java.util.function.Function;

public class WCatppuccinColorGrid<T> extends WVerticalList implements CatppuccinWidget {
    private static final double SWATCH_SIZE_FACTOR = 2;
    private static final double GRID_SPACING = 4;

    public Consumer<T> onColorSelected;

    private final String title;
    private final T[] values;
    private final Function<T, Color> colorOf;
    private final int columns;
    private final int maxVisibleRows;

    private int selectedIndex = -1;
    private boolean expanded;

    private WTable grid;
    private WCatppuccinButton toggleButton;

    public WCatppuccinColorGrid(String title, T[] values, Function<T, Color> colorOf, int columns, int maxVisibleRows) {
        this.title = title;
        this.values = values;
        this.colorOf = colorOf;
        this.columns = Math.max(1, columns);
        this.maxVisibleRows = Math.max(1, maxVisibleRows);
    }

    @Override
    public void init() {
        spacing = theme().pad();

        // Header
        WHorizontalList header = add(theme.horizontalList()).expandX().widget();
        header.add(theme.label(title)).expandX();

        toggleButton = (WCatppuccinButton) header.add(theme.button(toggleText())).widget();
        toggleButton.action = () -> setExpanded(!expanded);

        // Grid
        grid = add(theme.table()).expandX().widget();
        grid.horizontalSpacing = theme().scale(GRID_SPACING);
        grid.verticalSpacing = theme().scale(GRID_SPACING);

        rebuildGrid();
    }

    public void setExpanded(boolean value) {
        if (expanded == value) return;

        expanded = value;

        if (toggleButton != null) toggleButton.set(toggleText());
        if (grid != null) rebuildGrid();
    }

    public void select(T value) {
        selectedIndex = -1;

        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                selectedIndex = i;
                break;
            }
        }
    }

    public void selectMatching(Color color) {
        selectedIndex = -1;

        for (int i = 0; i < values.length; i++) {
            Color c = colorOf.apply(values[i]);
            if (c.r == color.r && c.g == color.g && c.b == color.b) {
                selectedIndex = i;
                break;
            }
        }
    }

    private String toggleText() {
        return expanded ? "Collapse" : "Show all";
    }

    private double swatchSize() {
        return theme().scale(theme().textHeight() * SWATCH_SIZE_FACTOR);
    }

    private int visibleCount() {
        int capacity = columns * maxVisibleRows;
        if (expanded) return values.length;
        if (values.length > capacity) return capacity - 1; // Reserve a slot for the indicator
        return values.length;
    }

    private void rebuildGrid() {
        grid.clear();

        int visible = visibleCount();

        for (int i = 0; i < visible; i++) {
            grid.add(new WSwatch(i)).expandX();
            if ((i + 1) % columns == 0) grid.row();
        }

        if (!expanded && values.length > visible) {
            grid.add(new WOverflowIndicator(values.length - visible));
        }
    }

    private class WSwatch extends WPressable implements CatppuccinWidget {
        private final int index;
        private Color color;
        private Color tickColor;
        private Color outlineColor;

        public WSwatch(int index) {
            this.index = index;
            instantTooltips = true;
            tooltip = String.valueOf(values[index]);
        }

        @Override
        public void init() {
            color = colorOf.apply(values[index]);
            tickColor = contrastColorFor(color);
            outlineColor = ColorUtils.withAlpha(tickColor, 0.6);
        }

        @Override
        protected void onPressed(int button) {
            selectedIndex = index;
            if (onColorSelected != null) onColorSelected.accept(values[index]);
        }

        @Override
        protected void onCalculateSize() {
            width = swatchSize();
            height = swatchSize();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            roundedRect().bounds(this)
                         .radius(smallRadius(), Corners.ALL)
                         .color(color)
                         .outline(outlineColor, outlineWidth())
                         .render();

            if (isSelected()) renderTick(renderer);
        }

        private void renderTick(GuiRenderer renderer) {
            double tickSize = width * 0.6;

            renderer.rotatedQuad(
                    x + (width - tickSize) / 2,
                    y + (height - tickSize) / 2,
                    tickSize,
                    tickSize,
                    0,
                    CatppuccinBuiltinIcons.TICK.texture(),
                    tickColor
            );
        }

        private Color contrastColorFor(Color color) {
            return ColorUtils.luma(color) >= 100 ? theme().surface1Color() : theme().overlay1Color();
        }

        private boolean isSelected() {
            return index == selectedIndex;
        }
    }

    private class WOverflowIndicator extends WPressable implements CatppuccinWidget {
        private final RichText overflowText;
        private Color backgroundColor;

        public WOverflowIndicator(int hiddenCount) {
            this.overflowText = RichText.of("+" + hiddenCount);
        }

        @Override
        public void init() {
            backgroundColor = ColorUtils.withAlpha(theme().surface0Color(), 0.6);
        }

        @Override
        protected void onPressed(int button) {
            setExpanded(true);
        }

        @Override
        protected void onCalculateSize() {
            width = swatchSize();
            height = swatchSize();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            roundedRect().bounds(this)
                         .radius(smallRadius(), Corners.ALL)
                         .color(backgroundColor)
                         .render();

            renderer().text(
                    overflowText,
                    x + (width - theme().textWidth(overflowText)) / 2,
                    y + (height - theme().textHeight()) / 2,
                    mouseOver ? theme().accentColor() : theme().textColor()
            );
        }
    }
}
