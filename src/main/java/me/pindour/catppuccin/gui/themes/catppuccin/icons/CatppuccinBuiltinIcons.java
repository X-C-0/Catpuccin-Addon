package me.pindour.catppuccin.gui.themes.catppuccin.icons;

import me.pindour.catppuccin.CatppuccinAddon;
import me.pindour.catppuccin.api.icons.CatppuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.systems.modules.Categories;

/*
 * Sources: https://www.svgrepo.com/collection/lightning-design-utility-icons/
 *          https://www.svgrepo.com/collection/clarity-project-icons/
 */
public enum CatppuccinBuiltinIcons {
    ARROW,
    BOOKMARK_NO,
    BOOKMARK_YES,
    COPY,
    CUBE,
    EDIT,
    EXPLOIT,
    EYE,
    IMPORT,
    MINUS,
    MOVEMENT,
    PLUS,
    QUESTION_MARK,
    RESET,
    SWORDS,
    USER,
    SEARCH;

    private final String path;
    private GuiTexture texture;

    CatppuccinBuiltinIcons() {
        this.path = "textures/icons/gui/" + name().toLowerCase() + ".png";
    }

    public static void init() {
        for (CatppuccinBuiltinIcons icon : values())
            icon.initIcon();

        // Init icons for Meteor
        CatppuccinIcons.registerCategoryIcon(Categories.Combat.name, SWORDS.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Player.name, USER.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Movement.name, MOVEMENT.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.Render.name, EYE.texture());
        CatppuccinIcons.registerCategoryIcon(Categories.World.name, CUBE.texture());
    }

    public void initIcon() {
        this.texture = GuiRenderer.addTexture(CatppuccinAddon.identifier(path));
    }

    public GuiTexture texture() {
        if (texture == null) throw new IllegalStateException("Icon " + name() + " not initialized.");
        return texture;
    }
}
