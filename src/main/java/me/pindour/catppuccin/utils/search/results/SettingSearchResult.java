package me.pindour.catppuccin.utils.search.results;

import me.pindour.catppuccin.utils.search.SearchResult;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

import java.util.ArrayList;
import java.util.List;

public record SettingSearchResult(Setting<?> setting, SettingGroup group, int score) implements SearchResult {

    @Override
    public String title() {
        return setting.title;
    }

    @Override
    public String description() {
        List<String> path = new ArrayList<>();

        if (setting.module != null) {
            path.add(setting.module.category.name);
            path.add(setting.module.title);
        } else {
            path.add("General");
        }

        if (group != null) {
            path.add(group.name);
        }
        
        return "Setting in: " + String.join(" > ", path);
    }
}