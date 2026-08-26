package me.pindour.catppuccin.gui.widgets.tabs;

import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WTabView extends WVerticalList {
    protected final List<CatppuccinTab> tabs;
    protected CatppuccinTab activeTab;

    protected WHeader header;
    protected WContainer content;
    private boolean rebuilding;

    public Consumer<CatppuccinTab> onTabChange;

    public WTabView(List<CatppuccinTab> tabs, CatppuccinTab initialTab) {
        this.tabs = new ArrayList<>(tabs);
        this.activeTab = initialTab;
        this.spacing = 0;
    }

    @Override
    public void init() {
        header = createHeader();
        add(header).centerX();

        content = createContent();
        add(content).expandX();
        rebuildContent();
    }

    protected WHeader createHeader() {
        return new WHeader();
    }

    protected WContainer createContent() {
        return null;
    }

    protected WTabButton createTabButton(CatppuccinTab tab) {
        return new WTabButton(tab);
    }

    public boolean isTabActive(CatppuccinTab tab) {
        return activeTab == tab;
    }

    private void rebuildContent() {
        if (rebuilding) return;

        rebuilding = true;

        content.clear();
        activeTab.build(content);

        rebuilding = false;
    }

    protected class WHeader extends WHorizontalList {
        @Override
        public void init() {
            for (CatppuccinTab tab : tabs) {
                add(createTabButton(tab));
            }
        }

        protected void onTabChange(CatppuccinTab tab) { }
    }

    protected class WTabButton extends WPressable {
        protected final CatppuccinTab tab;

        public WTabButton(CatppuccinTab tab) {
            this.tab = tab;
        }

        @Override
        protected void onPressed(int button) {
            if (activeTab == tab) return;

            activeTab = tab;
            rebuildContent();

            if (header != null) header.onTabChange(tab);
            if (onTabChange != null) onTabChange.accept(tab);
        }

        @Override
        protected void onCalculateSize() {
            double padH = theme.textHeight();
            double padV = theme.textHeight() / 2;

            width = padH + theme.textWidth(tab.name()) + padH;
            height = padV + theme.textHeight() + padV;
        }
    }
}
