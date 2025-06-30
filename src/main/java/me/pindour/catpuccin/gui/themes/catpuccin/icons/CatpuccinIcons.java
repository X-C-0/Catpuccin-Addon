package me.pindour.catpuccin.gui.themes.catpuccin.icons;

import me.pindour.catpuccin.CatpuccinAddon;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;

/*
 * Sources: https://www.svgrepo.com/collection/lightning-design-utility-icons/
 *          https://www.svgrepo.com/collection/clarity-project-icons/
 */
public enum CatpuccinIcons {
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

    CatpuccinIcons() {
        this.path = "textures/icons/gui/" + name().toLowerCase() + ".png";
    }

    public static void init() {
        for (CatpuccinIcons icon : values())
            icon.initIcon();
    }

    public void initIcon() {
        this.texture = GuiRenderer.addTexture(CatpuccinAddon.identifier(path));
    }

    public GuiTexture texture() {
        if (texture == null) throw new IllegalStateException("Icon " + name() + " not initialized.");
        return texture;
    }
}
