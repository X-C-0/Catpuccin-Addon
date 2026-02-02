package me.pindour.catppuccin.utils.search;

public interface SearchResult {
    int score();
    String title();
    String description();
}
