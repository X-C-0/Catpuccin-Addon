package me.pindour.catpuccin;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class CatpuccinAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    public static final String MOD_ID = "catpuccin-addon";

    @Override
    public void onInitialize() {
        LOG.info("Initializing Catpuccin Addon");

        GuiThemes.add(new CatpuccinGuiTheme());
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public String getPackage() {
        return "me.pindour.catpuccin";
    }
}
