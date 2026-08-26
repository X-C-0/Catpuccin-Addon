package me.pindour.catppuccin.gui.widgets.tabs;

import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabBuilder {
    private final List<CatppuccinTab> tabs = new ArrayList<>();

    public TabBuilder tab(String name, Consumer<WContainer> contentBuilder) {
        tabs.add(new CatppuccinTab(name, contentBuilder));
        return this;
    }

    public List<CatppuccinTab> build() {
        return List.copyOf(tabs);
    }
}
