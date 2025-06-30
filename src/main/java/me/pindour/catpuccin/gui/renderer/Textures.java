package me.pindour.catpuccin.gui.renderer;

import me.pindour.catpuccin.CatpuccinAddon;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;

public enum Textures {
    CIRCLE;

    private final String path;
    private GuiTexture texture;

    Textures() {
        this.path = "textures/" + name().toLowerCase() + ".png";
    }

    public static void init() {
        for (Textures texture : values())
            texture.initTexture();
    }

    public void initTexture() {
        this.texture = GuiRenderer.addTexture(CatpuccinAddon.identifier(path));
    }

    public GuiTexture texture() {
        if (texture == null) throw new IllegalStateException("Texture " + name() + " not initialized.");
        return texture;
    }
}
