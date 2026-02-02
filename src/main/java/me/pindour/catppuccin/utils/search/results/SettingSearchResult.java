package me.pindour.catppuccin.utils.search.results;

import me.pindour.catppuccin.utils.search.SearchResult;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public record SettingSearchResult(Setting<?> setting, SettingGroup group, int score) implements SearchResult {

    @Override
    public String title() {
        return setting.title;
    }

    @Override
    public String description() {
        return "Setting in: " + setting.module.category + " > " + setting.module.title + " > " + group.name;
    }
}