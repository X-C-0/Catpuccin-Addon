package me.pindour.catppuccin.gui.themes.catppuccin.icons;

import me.pindour.catppuccin.CatppuccinAddon;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;

/*
 * Sources: https://www.svgrepo.com/collection/lightning-design-utility-icons/
 *          https://www.svgrepo.com/collection/clarity-project-icons/
 */
public enum CatppuccinIcons {
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

    CatppuccinIcons() {
        this.path = "textures/icons/gui/" + name().toLowerCase() + ".png";
    }

    public static void init() {
        for (CatppuccinIcons icon : values())
            icon.initIcon();
    }

    public void initIcon() {
        this.texture = GuiRenderer.addTexture(CatppuccinAddon.identifier(path));
    }

    public GuiTexture texture() {
        if (texture == null) throw new IllegalStateException("Icon " + name() + " not initialized.");
        return texture;
    }
}
