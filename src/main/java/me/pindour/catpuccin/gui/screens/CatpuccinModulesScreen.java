package me.pindour.catpuccin.gui.screens;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.WCatpuccinWindow;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static meteordevelopment.meteorclient.utils.Utils.getWindowHeight;
import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;

//? if >1.21.4
import net.minecraft.util.Pair;

public class CatpuccinModulesScreen extends TabScreen {
    private final CatpuccinGuiTheme theme;
    private WCategoryController controller;

    private boolean showGrid = false;
    private boolean shouldSnap;
    private int gridSize;

    public CatpuccinModulesScreen(GuiTheme theme) {
        super(theme, Tabs.get().getFirst());
        this.theme = (CatpuccinGuiTheme) theme;
    }

    @Override
    public void initWidgets() {
        shouldSnap = theme.snapModuleCategories.get();
        gridSize = theme.snappingGridSize.get();

        // Categories
        controller = add(new WCategoryController()).widget();

        // Help
        WVerticalList help = add(theme.verticalList()).pad(4).bottom().widget();
        help.add(theme.label("Left click - Toggle module"));
        help.add(theme.label("Right click - Open module settings"));

        // Credit
        add(theme.label("Catpuccin Theme by Pindour")).bottom().right();
    }

    @Override
    protected void init() {
        super.init();
        controller.refresh();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);

        if (!showGrid) return;

        int color = theme.overlay0Color().copy().a(60).getPacked();
        int windowWidth = Utils.getWindowWidth();
        int windowHeight = Utils.getWindowHeight();

        for (int x = 0; x <= windowWidth; x += gridSize) {
            context.drawVerticalLine(x, 0, windowHeight, color);
        }

        for (int y = 0; y <= windowHeight; y += gridSize) {
            context.drawHorizontalLine(0, windowWidth, y, color);
        }
    }

    // Category

    protected WWindow createCategory(WContainer c, Category category, List<Module> moduleList) {
        WCatpuccinWindow w = (WCatpuccinWindow) theme.window(category.name);
        w.id = category.name;
        w.padding = 0;
        w.spacing = 0;

        if (shouldSnap) w.initSnapping(this, gridSize);

        double size = theme.scale(16);

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.icon(size, getIconForCategory(category))).centerY().pad(4).padHorizontal(10);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;
        w.view.maxHeight -= 120;

        for (Module module : moduleList) {
            w.add(theme.module(module)).expandX();
        }

        return w;
    }

    // Search

    protected void createSearchW(WContainer w, String text) {
        if (!text.isEmpty()) {
            // Titles
            //? if <=1.21.4 {
            /*Set<Module> modules = Modules.get().searchTitles(text);
            *///?} else {
            List<Pair<Module, String>> modules = Modules.get().searchTitles(text);
            //?}
            
            if (!modules.isEmpty()) {
                WSection section = w.add(theme.section("Modules")).expandX().widget();
                section.spacing = 0;

                int count = 0;

                //? if <=1.21.4 {
                /*for (Module module : modules) {
                    if (count >= Config.get().moduleSearchCount.get() || count >= modules.size()) break;
                    section.add(theme.module(module)).expandX();
                    count++;
                }
                *///?} else {
                for (Pair<Module, String> p : modules) {
                    if (count >= Config.get().moduleSearchCount.get() || count >= modules.size()) break;
                    section.add(theme.module(p.getLeft(), p.getRight())).expandX();
                    count++;
                }
                //?}
            }

            // Settings
            Set<Module> settings = Modules.get().searchSettingTitles(text);

            if (!settings.isEmpty()) {
                WSection section = w.add(theme.section("Settings")).expandX().widget();
                section.spacing = 0;

                int count = 0;
                for (Module module : settings) {
                    if (count >= Config.get().moduleSearchCount.get() || count >= settings.size()) break;
                    section.add(theme.module(module)).expandX();
                    count++;
                }
            }
        }
    }

    protected WWindow createSearch(WContainer c) {
        WCatpuccinWindow w = (WCatpuccinWindow) theme.window("Search");
        w.id = "search";

        if (shouldSnap) w.initSnapping(this, gridSize);

        double size = theme.scale(16);

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.icon(size, CatpuccinIcons.SEARCH)).centerY().pad(4).padHorizontal(10);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.maxHeight -= 20;

        WVerticalList l = theme.verticalList();

        WTextBox text = w.add(theme.textBox("")).minWidth(140).expandX().padBottom(4).widget();
        text.setFocused(true);
        text.action = () -> {
            l.clear();
            createSearchW(l, text.get());
        };

        w.add(l).expandX();
        createSearchW(l, text.get());

        return w;
    }

    // Favorites

    protected Cell<WWindow> createFavorites(WContainer c) {
        boolean hasFavorites = Modules.get().getAll().stream().anyMatch(module -> module.favorite);
        if (!hasFavorites) return null;

        WCatpuccinWindow w = (WCatpuccinWindow) theme.window("Favorites");
        w.id = "favorites";
        w.padding = 0;
        w.spacing = 0;

        if (shouldSnap) w.initSnapping(this, gridSize);

        double size = theme.scale(16);

        if (theme.categoryIcons()) {
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.icon(size, CatpuccinIcons.BOOKMARK_YES)).centerY().pad(4).padHorizontal(10);
        }

        Cell<WWindow> cell = c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        createFavoritesW(w);
        return cell;
    }

    protected boolean createFavoritesW(WWindow w) {
        List<Module> modules = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            if (module.favorite) {
                modules.add(module);
            }
        }

        modules.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name));

        for (Module module : modules) {
            w.add(theme.module(module)).expandX();
        }

        return !modules.isEmpty();
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Modules.get());
    }

    @Override
    public boolean fromClipboard() {
        return NbtUtils.fromClipboard(Modules.get());
    }

    @Override
    public void reload() {}

    // Stuff

    protected class WCategoryController extends WContainer {
        public final List<WWindow> windows = new ArrayList<>();
        private Cell<WWindow> favorites;

        @Override
        public void init() {
            List<Module> moduleList = new ArrayList<>();
            for (Category category : Modules.loopCategories()) {
                for (Module module : Modules.get().getGroup(category)) {
                    if (!Config.get().hiddenModules.get().contains(module)) {
                        moduleList.add(module);
                    }
                }

                // Ensure empty categories are not shown
                if (!moduleList.isEmpty()) {
                    windows.add(createCategory(this, category, moduleList));
                    moduleList.clear();
                }
            }

            refresh();

            windows.add(createSearch(this));
        }

        protected void refresh() {
            if (favorites == null) {
                favorites = createFavorites(this);
                if (favorites != null) windows.add(favorites.widget());
            }
            else {
                favorites.widget().clear();

                if (!createFavoritesW(favorites.widget())) {
                    remove(favorites);
                    windows.remove(favorites.widget());
                    favorites = null;
                }
            }
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double pad = theme.scale(4);
            double h = theme.scale(40);

            double x = this.x + pad;
            double y = this.y;

            for (Cell<?> cell : cells) {
                double windowWidth = getWindowWidth();
                double windowHeight = getWindowHeight();

                if (x + cell.width > windowWidth) {
                    x = x + pad;
                    y += h;
                }

                if (x > windowWidth) {
                    x = windowWidth / 2.0 - cell.width / 2.0;
                    if (x < 0) x = 0;
                }
                if (y > windowHeight) {
                    y = windowHeight / 2.0 - cell.height / 2.0;
                    if (y < 0) y = 0;
                }

                cell.x = x;
                cell.y = y;

                cell.width = cell.widget().width;
                cell.height = cell.widget().height;

                cell.alignWidget();

                x += cell.width + pad;
            }
        }
    }

    private CatpuccinIcons getIconForCategory(Category category) {
        CatpuccinIcons icon;
        switch (category.name) {
            case "Combat" -> icon = CatpuccinIcons.SWORDS;
            case "Player" -> icon = CatpuccinIcons.USER;
            case "Movement" -> icon = CatpuccinIcons.MOVEMENT;
            case "Render" -> icon = CatpuccinIcons.EYE;
            case "World" -> icon = CatpuccinIcons.CUBE;
            default -> icon = CatpuccinIcons.QUESTION_MARK;
        }

        return icon;
    }

    public void showGrid(boolean show) {
        showGrid = show;
    }

    public boolean showGrid() {
        return showGrid;
    }
}
