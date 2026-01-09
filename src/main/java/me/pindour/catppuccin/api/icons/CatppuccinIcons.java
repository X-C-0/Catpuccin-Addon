package me.pindour.catppuccin.api.icons;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CatppuccinIcons {
    private static final Map<String, GuiTexture> CATEGORY_ICONS = new HashMap<>();

    /**
     * Register a custom icon for a category.
     *
     * <p>Example:
     * <pre>{@code
     * Category CATEGORY = new Category("MyAddon", Items.DIRT.getDefaultInstance());
     * GuiTexture texture = GuiRenderer.addTexture(...);
     *
     * CatppuccinBuiltinIcons.registerCategoryIcon(CATEGORY.name, texture);
     * }</pre>
     *
     * @param categoryName The exact category name (case-sensitive)
     * @param texture GuiTexture for the category
     */
    public static void registerCategoryIcon(String categoryName, GuiTexture texture) {
        CATEGORY_ICONS.put(categoryName, texture);
    }

    /**
     * Register a custom icon for a category using an identifier.
     *
     * <p>Example:
     * <pre>{@code
     * Category CATEGORY = new Category("MyAddon", Items.DIRT.getDefaultInstance());
     *
     * CatppuccinBuiltinIcons.registerCategoryIcon(
     *     CATEGORY.name,
     *     new Identifier("myaddon", "textures/icons/custom.png")
     * );
     * }</pre>
     *
     * @param categoryName The exact category name (case-sensitive)
     * @param identifier Texture identifier (e.g. "modid:textures/icon.png")
     */
    public static void registerCategoryIcon(String categoryName, Identifier identifier) {
        GuiTexture texture = GuiRenderer.addTexture(identifier);
        CATEGORY_ICONS.put(categoryName, texture);
    }

    /**
     * Get registered custom icon for a category.
     * Returns {@code null} if no custom icon is registered.
     */
    public static GuiTexture getCategoryIcon(String categoryName) {
        return CATEGORY_ICONS.get(categoryName);
    }

    /**
     * Check if a category has a custom icon registered.
     */
    public static boolean hasCategoryIcon(String categoryName) {
        return CATEGORY_ICONS.containsKey(categoryName);
    }
}
