package me.pindour.catppuccin.utils.search;

import me.pindour.catppuccin.utils.search.results.ModuleSearchResult;
import me.pindour.catppuccin.utils.search.results.SettingSearchResult;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchUtils {
    private static final int DEFAULT_MAX_SCORE = 40;

    /**
     * Performs a global search across all available searchable objects (modules, settings...) based on the provided query.
     * <p>
     * Best matches (lowest score) are placed at the beginning of the list.
     *
     * @param query The text to search for.
     * @return A combined and sorted list of {@link SearchResult}.
     */
    public static List<SearchResult> search(String query) {
        List<SearchResult> results = new ArrayList<>();

        results.addAll(searchModules(query, DEFAULT_MAX_SCORE));
        results.addAll(searchSettings(query, DEFAULT_MAX_SCORE));

        results.sort(Comparator.comparingInt(SearchResult::score));

        return results;
    }

    /**
     * Searches for modules matching the query by their title or aliases.
     *
     * @param query    The text to search for.
     * @param maxScore The maximum allowed difference score. Results with a score higher than
     * this value (worse matches) will be excluded.
     * @return A list of {@link ModuleSearchResult} objects meeting the criteria.
     */
    public static List<ModuleSearchResult> searchModules(String query, int maxScore) {
        List<ModuleSearchResult> results = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            int score = Utils.searchLevenshteinDefault(module.title, query, false);
            String matchedAlias = null;

            if (Config.get().moduleAliases.get()) {
                for (String alias : module.aliases) {
                    int aliasScore = Utils.searchLevenshteinDefault(alias, query, false);
                    if (aliasScore < score) {
                        score = aliasScore;
                        matchedAlias = Utils.nameToTitle(alias);
                    }
                }
            }

            if (score <= maxScore) {
                results.add(new ModuleSearchResult(module, matchedAlias, score));
            }
        }
        return results;
    }

    /**
     * Searches for individual settings within all registered modules.
     *
     * @param query    The text to search for.
     * @param maxScore The maximum allowed difference score. Results with a score higher than
     * this value (worse matches) will be excluded.
     * @return A list of {@link SettingSearchResult} objects meeting the criteria.
     */
    public static List<SettingSearchResult> searchSettings(String query, int maxScore) {
        List<SettingSearchResult> results = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            for (SettingGroup sg : module.settings) {
                for (Setting<?> setting : sg) {
                    if (setting.title == null || setting.title.isEmpty()) continue;

                    int score = Utils.searchLevenshteinDefault(setting.title, query, false);

                    if (score <= maxScore) {
                        results.add(new SettingSearchResult(setting, sg, score));
                    }
                }
            }
        }
        return results;
    }
}