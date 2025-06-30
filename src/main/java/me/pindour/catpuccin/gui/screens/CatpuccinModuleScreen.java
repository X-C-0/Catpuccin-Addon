package me.pindour.catpuccin.gui.screens;

import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinKeybind;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.events.meteor.ModuleBindChangedEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;

public class CatpuccinModuleScreen extends WindowScreen {
    private final CatpuccinGuiTheme theme;
    private final Module module;

    private WContainer settingsContainer;
    private WCatpuccinKeybind keybind;
    private WCheckbox active;

    public CatpuccinModuleScreen(CatpuccinGuiTheme theme, Module module) {
        super(theme, theme.favorite(module.favorite), module.title);
        ((WFavorite) window.icon).action = () -> module.favorite = ((WFavorite) window.icon).checked;

        this.theme = theme;
        this.module = module;
    }

    @Override
    public void initWidgets() {
        int pad = 6;

        // Description
        WVerticalList description = add(theme.verticalList()).padHorizontal(pad).padBottom(pad).widget();
        description.add(theme.label(module.description, getWindowWidth() / 4.0));

        if (module.addon != null && module.addon != MeteorClient.ADDON) {
            WHorizontalList addon = description.add(theme.horizontalList()).expandX().widget();
            addon.add(theme.label("From: ").color(theme.textSecondaryColor())).widget();
            addon.add(theme.label(module.addon.name).color(theme.accentColor())).widget();
        }

        // Keybind
        WHorizontalList bind = add(theme.horizontalList()).padHorizontal(pad).expandX().widget();

        keybind = bind.add(theme.catpuccinKeybind(module.keybind)).expandX().widget();
        keybind.actionOnSet = () -> Modules.get().setModuleToBind(module);

        WDropdown<BindAction> bindAction = bind.add(theme.dropdown(module.toggleOnBindRelease ? BindAction.HOLD : BindAction.TOGGLE)).widget();
        bindAction.action = () -> module.toggleOnBindRelease = bindAction.get().isHold();

        WButton reset = bind.add(theme.button(CatpuccinIcons.RESET.texture())).right().widget();
        reset.action = keybind::resetBind;

        add(theme.horizontalSeparator()).padVertical(4).expandX();

        // Settings
        if (!module.settings.groups.isEmpty()) {
            settingsContainer = add(theme.verticalList()).expandX().widget();
            settingsContainer.add(theme.settings(module.settings)).expandX();
        }

        // Custom widget
        WWidget widget = module.getWidget(theme);

        if (widget != null) {
            add(theme.horizontalSeparator()).expandX();
            Cell<WWidget> cell = add(widget);
            if (widget instanceof WContainer) cell.expandX();
        }

        if (!module.settings.groups.isEmpty() || widget != null)
            add(theme.horizontalSeparator()).padVertical(4).expandX();

        // Bottom
        WHorizontalList bottom = add(theme.horizontalList()).padHorizontal(pad).expandX().widget();

        // Active
        active = bottom.add(theme.checkbox(module.isActive())).widget();
        active.action = () -> {
            if (module.isActive() != active.checked) module.toggle();
        };

        bottom.add(theme.label(RichText.of("Active"))).expandCellX().padLeft(4);

        // Config sharing
        WHorizontalList sharing = bottom.add(theme.horizontalList()).right().widget();
        WButton copy = sharing.add(theme.button(CatpuccinIcons.COPY.texture())).widget();
        copy.action = () -> {
            if (toClipboard()) {
                OkPrompt.create()
                        .title("Module copied!")
                        .message("The settings for this module are now in your clipboard.")
                        .message("You can also copy settings using Ctrl+C.")
                        .message("Settings can be imported using Ctrl+V or the paste button.")
                        .id("config-sharing-guide")
                        .show();
            }
        };
        copy.tooltip = "Copy config";

        WButton paste = sharing.add(theme.button(CatpuccinIcons.IMPORT.texture())).widget();
        paste.action = this::fromClipboard;
        paste.tooltip = "Paste config";
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !Modules.get().isBinding();
    }

    @Override
    public void tick() {
        super.tick();

        module.settings.tick(settingsContainer, theme);
    }

    @EventHandler
    private void onModuleBindChanged(ModuleBindChangedEvent event) {
        keybind.reset();
    }

    @EventHandler
    private void onActiveModulesChanged(ActiveModulesChangedEvent event) {
        this.active.checked = module.isActive();
    }

    @Override
    public boolean toClipboard() {
        NbtCompound tag = new NbtCompound();

        tag.putString("name", module.name);

        NbtCompound settingsTag = module.settings.toTag();
        if (!settingsTag.isEmpty()) tag.put("settings", settingsTag);

        return NbtUtils.toClipboard(tag);
    }

    @Override
    public boolean fromClipboard() {
        NbtCompound tag = NbtUtils.fromClipboard();
        if (tag == null) return false;
        if (!tag.getString("name", "").equals(module.name)) return false;

        Optional<NbtCompound> settings = tag.getCompound("settings");

        if (settings.isPresent()) module.settings.fromTag(settings.get());
        else module.settings.reset();

        if (parent instanceof WidgetScreen p) p.reload();
        reload();

        return true;
    }

    private enum BindAction {
        TOGGLE(false),
        HOLD(true);

        private final boolean hold;

        BindAction(boolean hold) {
            this.hold = hold;
        }

        public boolean isHold() {
            return hold;
        }
    }
}
