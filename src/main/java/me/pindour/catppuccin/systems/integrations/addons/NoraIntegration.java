package me.pindour.catppuccin.systems.integrations.addons;

import me.noramibu.tweaks.category.CustomCategoryHelper;
import me.noramibu.tweaks.gui.screens.AddToCategoryScreen;
import me.pindour.catppuccin.systems.integrations.Integration;
import me.pindour.catppuccin.systems.integrations.Integrations;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.function.Consumer;

public class NoraIntegration implements Integration {

    @Override
    public String getAddonPackage() {
        return "me.noramibu.tweaks";
    }

    public static void ifPresent(Consumer<NoraIntegration> action) {
        Integrations.ifPresent(NoraIntegration.class, action);
    }

    public void initCustomCategories(WContainer container, List<WWindow> windows) {
        CustomCategoryHelper helper = new CustomCategoryHelper(container, windows);
        helper.refresh();
    }

    public WButton addToCategoryButton(GuiTheme theme, Module module) {
        WButton button = theme.button("Add to Custom Category");
        button.action = () -> MinecraftClient.getInstance().setScreen(new AddToCategoryScreen(theme, module));

        return button;
    }
}
