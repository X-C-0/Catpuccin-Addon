package me.pindour.catpuccin.gui.screens.settings;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.widgets.input.WMultiSelect;
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

public class CatpuccinEntityTypeListSettingScreen extends WindowScreen {
    private final EntityTypeListSetting setting;
    private final CatpuccinGuiTheme theme;

    List<WMultiSelect<?>> widgets = new ArrayList<>();
    WView multiSelectView;

    public CatpuccinEntityTypeListSettingScreen(CatpuccinGuiTheme theme, EntityTypeListSetting setting) {
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

        List<WMultiSelect.ItemInfo<EntityType<?>>> animals = new ArrayList<>();
        List<WMultiSelect.ItemInfo<EntityType<?>>> waterAnimals = new ArrayList<>();
        List<WMultiSelect.ItemInfo<EntityType<?>>> monsters = new ArrayList<>();
        List<WMultiSelect.ItemInfo<EntityType<?>>> ambient = new ArrayList<>();
        List<WMultiSelect.ItemInfo<EntityType<?>>> misc = new ArrayList<>();

        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            if (setting.filter != null && !setting.filter.test(entityType)) continue;

            String name = Names.get(entityType);
            boolean isSelected = setting.get().contains(entityType);
            WMultiSelect.ItemInfo<EntityType<?>> info = new WMultiSelect.ItemInfo<>(entityType, name, isSelected);

            switch (entityType.getSpawnGroup()) {
                case CREATURE -> animals.add(info);
                case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> waterAnimals.add(info);
                case MONSTER -> monsters.add(info);
                case AMBIENT -> ambient.add(info);
                case MISC -> misc.add(info);
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

    private void createMultiSelectWidget(String title, List<WMultiSelect.ItemInfo<EntityType<?>>> items) {
        if (items.isEmpty()) return;

        WMultiSelect<EntityType<?>> widget = multiSelectView.add(theme.multiSelect(title, items)).padVertical(6).expandX().widget();
        widget.onSelection = (entity, selected) -> {
            if (selected) setting.get().add(entity);
            else setting.get().remove(entity);
        };

        widgets.add(widget);
    }
}
