package me.pindour.catppuccin.gui.widgets.tabs;

import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TabBridge {
    private final List<TabPair> tabPairs = new ArrayList<>();

    public TabBridge(List<Tab> meteorTabs, Function<Tab, GuiTexture> iconResolver) {
        for (Tab meteorTab : meteorTabs) {
            GuiTexture icon = iconResolver != null ? iconResolver.apply(meteorTab) : null;
            CatppuccinTab catppuccinTab = new CatppuccinTab(meteorTab.name, icon, ignored -> {});

            tabPairs.add(new TabPair(meteorTab, catppuccinTab));
        }
    }

    public List<CatppuccinTab> catppuccinTabs() {
        return tabPairs.stream()
                       .map(TabPair::catppuccin)
                       .toList();
    }

    public CatppuccinTab catppuccinTab(Tab meteorTab) {
        for (TabPair pair : tabPairs) {
            if (pair.meteor() == meteorTab) return pair.catppuccin();
        }
        return null;
    }

    public Tab meteorTab(CatppuccinTab catppuccinTab) {
        for (TabPair pair : tabPairs) {
            if (pair.catppuccin() == catppuccinTab) return pair.meteor();
        }
        return null;
    }

    public record TabPair(Tab meteor, CatppuccinTab catppuccin) { }
}