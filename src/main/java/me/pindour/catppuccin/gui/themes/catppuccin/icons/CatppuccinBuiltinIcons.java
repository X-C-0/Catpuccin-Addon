package me.pindour.catppuccin.gui.themes.catppuccin.icons;

import me.pindour.catppuccin.CatppuccinAddon;
import me.pindour.catppuccin.api.icons.CatppuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.builtin.ConfigTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.FriendsTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.GuiTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.HudTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.MacrosTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.ModulesTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.PathManagerTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.ProfilesTab;
import meteordevelopment.meteorclient.systems.modules.Categories;

import java.util.Locale;

/*
 * Sources: https://icon-sets.iconify.design/solar/
 *          https://app.iconsax.io/?corner=Rounded
 */
public enum CatppuccinBuiltinIcons {
    ARROW,
    BOOKMARK_NO,
    BOOKMARK_YES,
    BRUSH,
    COPY,
    CUBE,
    EDIT,
    EYE,
    GRID,
    IMPORT,
    MINUS,
    MOUSE,
    MOVEMENT,
    PEOPLE,
    PERSON,
    PLUS,
    QUESTION_MARK,
    RESET,
    SEARCH,
    SETTING,
    SWORD,
    TICK;

    private final String path;
    private GuiTexture texture;

    CatppuccinBuiltinIcons() {
        this.path = "textures/icons/gui/" + name().toLowerCase(Locale.ROOT) + ".png";
    }

    public static void init() {
        for (CatppuccinBuiltinIcons icon : values())
            icon.initIcon();

        // Init icons for Meteor
        CatppuccinIcons.registerCategoryIcon(Categories.Combat.name, SWORD.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Player.name, PERSON.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Movement.name, MOVEMENT.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Render.name, EYE.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.World.name, CUBE.texture());

        // Init icons for Meteor tabs
        CatppuccinIcons.registerTabIcon(ModulesTab.class, CUBE.texture());
        CatppuccinIcons.registerTabIcon(ConfigTab.class, SETTING.texture());
        CatppuccinIcons.registerTabIcon(GuiTab.class, BRUSH.texture());
        CatppuccinIcons.registerTabIcon(HudTab.class, GRID.texture());
        CatppuccinIcons.registerTabIcon(FriendsTab.class, PEOPLE.texture());
        CatppuccinIcons.registerTabIcon(MacrosTab.class, MOUSE.texture());
        CatppuccinIcons.registerTabIcon(ProfilesTab.class, PERSON.texture());
        CatppuccinIcons.registerTabIcon(PathManagerTab.class, MOVEMENT.texture());
    }

    public void initIcon() {
        try {
            this.texture = GuiRenderer.addTexture(CatppuccinAddon.identifier(path));
        } catch (Exception e) {
            throw new RuntimeException("Icon '" + name() + "' could not be loaded.");
        }
    }

    public GuiTexture texture() {
        if (texture == null) throw new IllegalStateException("Icon " + name() + " not initialized.");
        return texture;
    }
}
