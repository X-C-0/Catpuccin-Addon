package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.api.render.Corners;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.api.text.TextScale;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import me.pindour.catppuccin.gui.widgets.input.WSearch;
import me.pindour.catppuccin.utils.ColorUtils;
import me.pindour.catppuccin.utils.search.SearchResult;
import me.pindour.catppuccin.utils.search.results.ModuleSearchResult;
import me.pindour.catppuccin.utils.search.results.SettingSearchResult;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinSearch extends WSearch implements CatppuccinWidget {

    @Override
    protected WSearchHeader createHeader(WSearch search) {
        return new WCatppuccinHeader(search);
    }

    @Override
    protected WResultsContainer createResultsContainer() {
        return new WCatppuccinResultsContainer();
    }

    @Override
    protected WSearchResult createSearchResult(SearchResult result) {
        return new WCatppuccinResult(result);
    }

    private static class WCatppuccinHeader extends WSearchHeader implements CatppuccinWidget {

        public WCatppuccinHeader(WSearch search) {
            super(search);
        }

        @Override
        public void init() {
            CatppuccinGuiTheme theme = theme();

            // Row container
            WHorizontalList row = add(theme.horizontalList()).expandX().pad(theme.scale(12)).widget();

            // Search texture
            row.add(theme.texture(CatppuccinBuiltinIcons.SEARCH.texture(), theme.textHeight())).padRight(10).center();

            // Search textbox
            WCatppuccinTextBox textBox = (WCatppuccinTextBox) theme.textBox("", "Search for modules...");
            textBox.shouldRenderBackground(false);

            row.add(textBox).expandX();
            search.initTextBox(textBox);

            // Hint label
            row.add(theme.label("ESC to close"));
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            roundedRect().bounds(this)
                         .color(theme().crustColor())
                         .radius(radius(), Corners.TOP)
                         .render();
        }
    }

    private static class WCatppuccinResultsContainer extends WResultsContainer implements CatppuccinWidget {
        @Override
        public void init() {
            super.init();

            addDirect(theme.label("Left click to toggle module; Right click open the module's settings.")).pad(theme.pad()).centerX();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();

            roundedRect().bounds(this)
                         .color(ColorUtils.withAlpha(theme.baseColor(), theme.windowOpacity()))
                         .radius(radius(), Corners.BOTTOM)
                         .render();
        }
    }

    private static class WCatppuccinResult extends WSearchResult implements CatppuccinWidget {
        private CatppuccinGuiTheme theme;

        public WCatppuccinResult(SearchResult result) {
            super(result);
        }

        @Override
        public void init() {
            theme = theme();

            // Row container
            WHorizontalList row = add(theme.horizontalList()).expandX().pad(6).widget();

            // Result type icon
            row.add(new WResultType(result)).pad(theme.pad()).center();

            // Result info container
            WVerticalList infoColumn = row.add(theme.verticalList()).expandX().widget();

            // Result title
            infoColumn.add(theme.label(RichText.of(result.title())));

            // Result description
            RichText desc = RichText.of(result.description()).scale(TextScale.SMALL.get());
            WLabel descLabel = infoColumn.add(theme.label(desc)).widget();
            descLabel.color = theme.textSecondaryColor();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!mouseOver) return;

            Color outlineColor = ColorUtils.withAlpha(
                    theme.accentColor(),
                    theme.backgroundOpacity() * 0.5
            );

            background(theme.surface0Color(), outlineColor).render();
        }

        public static class WResultType extends WWidget implements CatppuccinWidget {
            private final SearchResult result;
            private RichText text;
            private Color color;

            public WResultType(SearchResult result) {
                this.result = result;
            }

            @Override
            public void init() {
                color = getColor();
                text = RichText.of(getLetter()).scale(TextScale.LARGE.get());
            }

            @Override
            public void calculateSize() {
                double pad = theme.pad();

                width = height = pad + theme.textHeight() + pad;
            }

            @Override
            protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
                roundedRect().bounds(this)
                             .color(ColorUtils.withAlpha(color, 60))
                             .radius(smallRadius())
                             .render();

                double x = this.x + width / 2 - theme().textWidth(text) / 2;
                double y = this.y + height / 2 - theme().textHeight(text) / 2;

                renderer().text(text, x, y, color);
            }

            private Color getColor() {
                return switch (result) {
                    case ModuleSearchResult r -> r.hasAlias() ? theme().yellowColor() : theme().greenColor();
                    case SettingSearchResult ignored -> theme().blueColor();
                    default -> theme().textSecondaryColor();
                };
            }

            private String getLetter() {
                return switch (result) {
                    case ModuleSearchResult r -> r.hasAlias() ? "A" : "M";
                    case SettingSearchResult ignored -> "S";
                    default -> "-";
                };
            }
        }
    }
}