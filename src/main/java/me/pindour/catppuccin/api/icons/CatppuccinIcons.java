package me.pindour.catppuccin.api.icons;

import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CatppuccinIcons {
    private static final Map<String, GuiTexture> CATEGORY_ICONS = new HashMap<>();
    private static final Map<Class<? extends Tab>, GuiTexture> TAB_ICONS = new HashMap<>();

    /**
     * Register a custom icon for a category.
     *
     * <p>Example:
     * <pre>{@code
     * Category CATEGORY = new Category("MyAddon", Items.DIRT.getDefaultInstance());
     * GuiTexture texture = GuiRenderer.addTexture(...);
     *
     * CatppuccinIcons.registerCategoryIcon(CATEGORY.name, texture);
     * }</pre>
     *
     * @param categoryName The exact category name (case-sensitive)
     * @param texture GuiTexture for the category
     */
    public static void registerCategoryIcon(String categoryName, GuiTexture texture) {
        CATEGORY_ICONS.put(categoryName, texture);
    }

    /**
     * Get registered custom icon for a category.
     * Returns {@code null} if no custom icon is registered.
     */
    @Nullable
    public static GuiTexture getCategoryIcon(String categoryName) {
        return CATEGORY_ICONS.get(categoryName);
    }

    /**
     * Register a custom icon for a tab.
     *
     * <p>Example:
     * <pre>{@code
     * GuiTexture texture = GuiRenderer.addTexture(...);
     *
     * CatppuccinIcons.registerTabIcon(YourAddonTab.class, texture);
     * }</pre>
     *
     * @param tabClass The tab class
     * @param texture GuiTexture for the tab
     */
    public static void registerTabIcon(Class<? extends Tab> tabClass, GuiTexture texture) {
        TAB_ICONS.put(tabClass, texture);
    }

    /**
     * Get registered custom icon for a tab.
     * Returns {@code null} if no custom icon is registered.
     */
    @Nullable
    public static GuiTexture getTabIcon(Class<? extends Tab> tabClass) {
        return TAB_ICONS.get(tabClass);
    }
}
