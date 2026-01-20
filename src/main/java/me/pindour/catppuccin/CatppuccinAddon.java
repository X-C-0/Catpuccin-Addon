package me.pindour.catppuccin;

import me.pindour.catppuccin.api.render.RoundedRect;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import com.mojang.logging.LogUtils;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class CatppuccinAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    public static final String MOD_ID = "catppuccin-addon";

    @Override
    public void onInitialize() {
        LOG.info("Initializing Catppuccin Addon");

        GuiThemes.add(new CatppuccinGuiTheme());

        RoundedRect.get().registerRenderer(CatppuccinRenderer.get());
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public String getPackage() {
        return "me.pindour.catppuccin";
    }
}
