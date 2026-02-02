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
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinSearch extends WSearch implements CatppuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();
        Color shadowColor = ColorUtils.withAlpha(theme.crustColor(), 0.4);

        // Shadow rectangle
        int shadowOffset = 2;
        roundedRect().pos(x - shadowOffset, y - shadowOffset)
                     .size(width + shadowOffset * 2, height + shadowOffset * 2)
                     .radius(radius() + shadowOffset)
                     .color(shadowColor)
                     .render();
    }

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
            row.add(theme.texture(CatppuccinBuiltinIcons.SEARCH.texture(), theme.textHeight())).center();

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

            background(getBackgroundColor(pressed, false), outlineColor).render();
        }

        public static class WResultType extends WContainer implements CatppuccinWidget {
            private final SearchResult result;
            private Color color;

            public WResultType(SearchResult result) {
                this.result = result;
            }

            @Override
            public void init() {
                color = getColor();

                add(theme().texture(getIcon(), theme.textHeight()).color(color)).pad(theme.pad()).center();
            }

            @Override
            protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
                roundedRect().bounds(this)
                             .color(ColorUtils.withAlpha(color, 60))
                             .radius(smallRadius())
                             .render();
            }

            private Color getColor() {
                return switch (result) {
                    case ModuleSearchResult r -> r.hasAlias() ? theme().yellowColor() : theme().greenColor();
                    case SettingSearchResult ignored -> theme().blueColor();
                    default -> theme().textSecondaryColor();
                };
            }

            private GuiTexture getIcon() {
                return switch (result) {
                    case ModuleSearchResult ignored -> CatppuccinBuiltinIcons.CUBE.texture();
                    case SettingSearchResult ignored -> CatppuccinBuiltinIcons.SETTING.texture();
                    default -> CatppuccinBuiltinIcons.QUESTION_MARK.texture();
                };
            }
        }
    }
}