package me.pindour.catppuccin.gui.widgets.input;

import me.pindour.catppuccin.utils.search.SearchResult;
import me.pindour.catppuccin.utils.search.results.ModuleSearchResult;
import me.pindour.catppuccin.utils.search.SearchUtils;
import me.pindour.catppuccin.utils.search.results.SettingSearchResult;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

//? >=1.21.5
import net.minecraft.client.input.MouseButtonEvent;

public abstract class WSearch extends WVerticalList {
    protected WSearchHeader header;
    protected WResultsContainer searchResults;

    protected WTextBox textBox;

    @Override
    public void init() {
        spacing = 0;

        header = add(createHeader(this)).expandX().widget();
        searchResults = add(createResultsContainer()).expandX().widget();
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();

        // This should prevent funny business on ultra-wide monitors (hopefully?)
        minWidth = Math.min(Utils.getWindowWidth() / 3.0, theme.scale(500));
    }

    public void initTextBox(WTextBox textBox) {
        this.textBox = textBox;
        this.textBox.action = this::updateResults;
        this.textBox.setFocused(true);
    }

    protected abstract WSearchHeader createHeader(WSearch search);

    protected abstract WResultsContainer createResultsContainer();

    protected abstract WSearchResult createSearchResult(SearchResult result);

    public void updateResults() {
        String query = textBox.get();
        searchResults.clear();

        if (query.isEmpty()) return;

        List<SearchResult> results = SearchUtils.search(query);

        for (SearchResult result : results) {
            searchResults.add(createSearchResult(result)).padHorizontal(theme.scale(8)).expandX();
        }
    }

    protected abstract static class WSearchHeader extends WHorizontalList {
        protected WSearch search;

        public WSearchHeader(WSearch search) {
            this.search = search;
        }
    }

    protected abstract static class WResultsContainer extends WVerticalList {
        protected WView view;

        @Override
        public void init() {
            view = addDirect(theme.view()).expandX().pad(theme.scale(8)).widget();
            view.hasScrollBar = false;
            view.spacing = 0;
            view.maxHeight = (Utils.getWindowHeight() / 3.0) * 2;
        }

        @Override
        public <T extends WWidget> Cell<T> add(T widget) {
            return view.add(widget);
        }

        public <T extends WWidget> Cell<T> addDirect(T widget) {
            return super.add(widget);
        }

        @Override
        public void clear() {
            view.clear();
        }
    }

    protected abstract static class WSearchResult extends WHorizontalList {
        protected boolean pressed;
        protected SearchResult result;

        public WSearchResult(SearchResult result) {
            this.result = result;
        }

        @Override
        public boolean onMouseClicked(MouseButtonEvent click, boolean doubled) {
            //? >=1.21.5
            int button = click.button();

            if (mouseOver && (button == GLFW_MOUSE_BUTTON_LEFT || button == GLFW_MOUSE_BUTTON_RIGHT))
                pressed = true;

            return pressed;
        }

        @Override
        public boolean onMouseReleased(MouseButtonEvent click) {
            if (pressed) {
                //? >=1.21.5
                int button = click.button();

                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    if (result instanceof ModuleSearchResult r) {
                        r.module().toggle();
                    }
                }

                if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                    switch (result) {
                        case ModuleSearchResult r -> mc.setScreen(theme.moduleScreen(r.module()));
                        case SettingSearchResult r -> mc.setScreen(theme.moduleScreen(r.setting().module));
                        default -> { } // ignored
                    }
                }

                pressed = false;
            }

            return false;
        }
    }
}