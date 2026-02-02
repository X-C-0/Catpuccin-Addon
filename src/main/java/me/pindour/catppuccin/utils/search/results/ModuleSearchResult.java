package me.pindour.catppuccin.utils.search.results;

import me.pindour.catppuccin.utils.search.SearchResult;
import meteordevelopment.meteorclient.systems.modules.Module;

public record ModuleSearchResult(Module module, String alias, int score) implements SearchResult {

    @Override
    public String title() {
        return hasAlias() ? alias : module.title;
    }

    @Override
    public String description() {
        if (hasAlias()) {
            return "Alias for: " + module.title + " (" + module.category.name + ")";
        }

        return "Module in: " + module.category.name;
    }

    @Override
    public int score() {
        return score;
    }

    public boolean hasAlias() {
        return alias != null;
    }
}