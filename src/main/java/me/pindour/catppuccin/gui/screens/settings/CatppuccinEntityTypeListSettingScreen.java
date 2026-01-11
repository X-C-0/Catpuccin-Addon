package me.pindour.catppuccin.gui.screens.settings;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.widgets.input.WMultiSelect;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

public class CatppuccinEntityTypeListSettingScreen extends WindowScreen {
    private final EntityTypeListSetting setting;
    private final CatppuccinGuiTheme theme;

    List<WMultiSelect<?>> widgets = new ArrayList<>();
    WView multiSelectView;

    public CatppuccinEntityTypeListSettingScreen(CatppuccinGuiTheme theme, EntityTypeListSetting setting) {
        super(theme, "Select entities");
        this.theme = theme;
        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        // Top bar to filter the entities
        WHorizontalList list = add(theme.horizontalList()).expandX().widget();

        WTextBox searchBox = list.add(theme.textBox("", "Search entities...")).padBottom(theme.pad()).minWidth(250).expandX().widget();
        searchBox.setFocused(true);

        WDropdown<WMultiSelect.FilterMode> filterDropdown = list.add(theme.dropdown("Show", WMultiSelect.FilterMode.ALL)).widget();

        filterDropdown.action = () -> {
            String query = searchBox.get();
            widgets.forEach(widget -> {
                widget.setFilterMode(filterDropdown.get());
                widget.updateFilter(query);
            });
        };

        // View with multi-select widgets
        multiSelectView = add(theme.view()).expandX().widget();
        multiSelectView.maxHeight -= 100;

        List<EntityType<?>> animals = new ArrayList<>();
        List<EntityType<?>> waterAnimals = new ArrayList<>();
        List<EntityType<?>> monsters = new ArrayList<>();
        List<EntityType<?>> ambient = new ArrayList<>();
        List<EntityType<?>> misc = new ArrayList<>();

        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            if (setting.filter != null && !setting.filter.test(entityType)) continue;

            switch (entityType.getSpawnGroup()) {
                case CREATURE -> animals.add(entityType);
                case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> waterAnimals.add(entityType);
                case MONSTER -> monsters.add(entityType);
                case AMBIENT -> ambient.add(entityType);
                case MISC -> misc.add(entityType);
            }
        }

        createMultiSelectWidget("Animals", animals);
        createMultiSelectWidget("Water Animals", waterAnimals);
        createMultiSelectWidget("Monsters", monsters);
        createMultiSelectWidget("Ambient", ambient);
        createMultiSelectWidget("Misc", misc);

        int totalCount = animals.size() + waterAnimals.size() + monsters.size() + ambient.size() + misc.size();
        if (totalCount <= 30) widgets.forEach(widget -> widget.setExpanded(true));

        searchBox.action = () -> {
            String query = searchBox.get().trim().toLowerCase();
            widgets.forEach(widget -> widget.updateFilter(query));
        };
    }

    private void createMultiSelectWidget(String title, List<EntityType<?>> items) {
        if (items.isEmpty()) return;

        WMultiSelect<EntityType<?>> multiSelect = theme.multiSelect(title, items)
                .label(Names::get)
                .isSelected(setting.get()::contains)
                .onSelectionChange((entity, selected) -> {
                    if (selected) setting.get().add(entity);
                    else setting.get().remove(entity);
                });

        multiSelectView.add(multiSelect).padVertical(6).expandX();
        widgets.add(multiSelect);
    }
}
