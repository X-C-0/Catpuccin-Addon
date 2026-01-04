package me.pindour.catpuccin.gui.themes.catpuccin;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.screens.settings.CatpuccinEntityTypeListSettingScreen;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.TextSize;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.WCatpuccinLabel;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable.WCatpuccinButton;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable.WCatpuccinCheckbox;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinDoubleEdit;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinIntEdit;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinKeybind;
import me.pindour.catpuccin.gui.widgets.pressable.WColorPicker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.settings.*;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.WItem;
import meteordevelopment.meteorclient.gui.widgets.WItemWithLabel;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.*;
import meteordevelopment.meteorclient.gui.widgets.input.WBlockPosEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.resource.language.I18n;
import org.apache.commons.lang3.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CatpuccinSettingsWidgetFactory extends SettingsWidgetFactory {
    private static final SettingColor WHITE = new SettingColor();
    private final CatpuccinGuiTheme theme;

    public CatpuccinSettingsWidgetFactory(CatpuccinGuiTheme theme) {
        super(theme);

        this.theme = theme;

        factories.put(BoolSetting.class, (table, setting) -> boolW(table, (BoolSetting) setting));
        factories.put(IntSetting.class, (table, setting) -> intW(table, (IntSetting) setting));
        factories.put(DoubleSetting.class, (table, setting) -> doubleW(table, (DoubleSetting) setting));
        factories.put(StringSetting.class, (table, setting) -> stringW(table, (StringSetting) setting));
        factories.put(EnumSetting.class, (table, setting) -> enumW(table, (EnumSetting<? extends Enum<?>>) setting));
        factories.put(ProvidedStringSetting.class, (table, setting) -> providedStringW(table, (ProvidedStringSetting) setting));
        factories.put(GenericSetting.class, (table, setting) -> genericW(table, (GenericSetting<?>) setting));
        factories.put(ColorSetting.class, (table, setting) -> colorW(table, (ColorSetting) setting));
        factories.put(KeybindSetting.class, (table, setting) -> keybindW(table, (KeybindSetting) setting));
        factories.put(BlockSetting.class, (table, setting) -> blockW(table, (BlockSetting) setting));
        factories.put(BlockListSetting.class, (table, setting) -> blockListW(table, (BlockListSetting) setting));
        factories.put(ItemSetting.class, (table, setting) -> itemW(table, (ItemSetting) setting));
        factories.put(ItemListSetting.class, (table, setting) -> itemListW(table, (ItemListSetting) setting));
        factories.put(EntityTypeListSetting.class, (table, setting) -> entityTypeListW(table, (EntityTypeListSetting) setting));
        factories.put(EnchantmentListSetting.class, (table, setting) -> enchantmentListW(table, (EnchantmentListSetting) setting));
        factories.put(ModuleListSetting.class, (table, setting) -> moduleListW(table, (ModuleListSetting) setting));
        factories.put(PacketListSetting.class, (table, setting) -> packetListW(table, (PacketListSetting) setting));
        factories.put(ParticleTypeListSetting.class, (table, setting) -> particleTypeListW(table, (ParticleTypeListSetting) setting));
        factories.put(SoundEventListSetting.class, (table, setting) -> soundEventListW(table, (SoundEventListSetting) setting));
        factories.put(StatusEffectAmplifierMapSetting.class, (table, setting) -> statusEffectAmplifierMapW(table, (StatusEffectAmplifierMapSetting) setting));
        factories.put(StatusEffectListSetting.class, (table, setting) -> statusEffectListW(table, (StatusEffectListSetting) setting));
        factories.put(StorageBlockListSetting.class, (table, setting) -> storageBlockListW(table, (StorageBlockListSetting) setting));
        factories.put(ScreenHandlerListSetting.class, (table, setting) -> screenHandlerListW(table, (ScreenHandlerListSetting) setting));
        factories.put(BlockDataSetting.class, (table, setting) -> blockDataW(table, (BlockDataSetting<?>) setting));
        factories.put(PotionSetting.class, (table, setting) -> potionW(table, (PotionSetting) setting));
        factories.put(StringListSetting.class, (table, setting) -> stringListW(table, (StringListSetting) setting));
        factories.put(BlockPosSetting.class, (table, setting) -> blockPosW(table, (BlockPosSetting) setting));
        factories.put(ColorListSetting.class, (table, setting) -> colorListW(table, (ColorListSetting) setting));
        factories.put(FontFaceSetting.class, (table, setting) -> fontW(table, (FontFaceSetting) setting));
        factories.put(Vector3dSetting.class, (table, setting) -> vector3dW(table, (Vector3dSetting) setting));
    }

    // Spacing

    private double settingSpacing() {
        return theme.pad();
    }

    // Setting groups

    @Override
    public WWidget create(GuiTheme theme, Settings settings, String filter) {
        WVerticalList list = theme.verticalList();

        List<RemoveInfo> removeInfoList = new ArrayList<>();

        // Add all settings
        for (SettingGroup group : settings.groups) {
            group(list, group, filter, removeInfoList);
        }

        // Calculate width and set it as minimum width
        list.calculateSize();
        list.minWidth = list.width;

        // Remove hidden settings
        for (RemoveInfo removeInfo : removeInfoList) {
            removeInfo.remove(list);
        }

        return list;
    }

    private void group(WVerticalList list, SettingGroup group, String filter, List<RemoveInfo> removeInfoList) {
        WSection section = list.add(theme.section(group.name, group.sectionExpanded)).expandX().pad(theme.pad()).widget();
        section.action = () -> group.sectionExpanded = section.isExpanded();

        WTable table = section.add(theme.table()).expandX().pad(theme.pad() * 2).widget();
        table.verticalSpacing = settingSpacing();

        RemoveInfo removeInfo = null;

        for (Setting<?> setting : group) {
            if (!Strings.CI.contains(setting.title, filter)) continue;

            boolean visible = setting.isVisible();
            setting.lastWasVisible = visible;
            if (!visible) {
                if (removeInfo == null) removeInfo = new RemoveInfo(section, table);
                removeInfo.markRowForRemoval();
            }

            Factory factory = getFactory(setting.getClass());
            if (factory != null) factory.create(table, setting);

            table.row();
        }

        if (removeInfo != null) removeInfoList.add(removeInfo);
    }

    private static class RemoveInfo {
        private final WSection section;
        private final WTable table;
        private final IntList rowIds = new IntArrayList();

        public RemoveInfo(WSection section, WTable table) {
            this.section = section;
            this.table = table;
        }

        public void markRowForRemoval() {
            rowIds.add(table.rowI());
        }

        public void remove(WVerticalList list) {
            for (int i = 0; i < rowIds.size(); i++) {
                table.removeRow(rowIds.getInt(i) - i);
            }

            if (table.cells.isEmpty()) list.cells.removeIf(cell -> cell.widget() == section);
        }
    }

    // Settings

    private void boolW(WTable table, BoolSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WCatpuccinCheckbox checkbox = (WCatpuccinCheckbox) list.add(theme.checkbox(setting.get())).widget();
        checkbox.action = () -> setting.set(checkbox.checked);

        title(list, setting).padLeft(theme.pad());

        reset(table, setting, () -> checkbox.setChecked(setting.get()), () -> list.mouseOver);
    }

    private void intW(WTable table, IntSetting setting) {
        WCatpuccinIntEdit edit = table.add(theme.catpuccinIntEdit(
                setting.title,
                setting.description,
                setting.get(),
                setting.min,
                setting.max,
                setting.sliderMin,
                setting.sliderMax,
                setting.noSlider
        )).expandX().widget();

        edit.action = () -> {
            if (!setting.set(edit.get())) edit.set(setting.get());
        };

        edit.header.add(reset(table, setting, () -> edit.set(setting.get()), edit::showReset).widget());
    }

    private void doubleW(WTable table, DoubleSetting setting) {
        WCatpuccinDoubleEdit edit = table.add(theme.catpuccinDoubleEdit(
                setting.title,
                setting.description,
                setting.get(),
                setting.min,
                setting.max,
                setting.sliderMin,
                setting.sliderMax,
                setting.decimalPlaces,
                setting.noSlider
        )).expandX().widget();

        Runnable action = () -> {
            if (!setting.set(edit.get())) edit.set(setting.get());
        };

        if (setting.onSliderRelease) edit.actionOnRelease = action;
        else edit.action = action;

        edit.header.add(reset(table, setting, () -> edit.set(setting.get()), edit::showReset).widget());
    }

    private void stringW(WTable table, StringSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        title(list, setting).padLeft(theme.pad());

        CharFilter filter = setting.filter == null ? (text, c) -> true : setting.filter;
        Cell<WTextBox> cell = list.add(theme.textBox(setting.get(), "", setting.title, filter, setting.renderer));
        if (setting.wide) cell.minWidth(Utils.getWindowWidth() - Utils.getWindowWidth() / 4.0);

        WTextBox textBox = cell.expandX().widget();
        textBox.action = () -> setting.set(textBox.get());

        reset(table, setting, () -> textBox.set(setting.get()), () -> list.mouseOver);
    }

    private void stringListW(WTable table, StringListSetting setting) {
        WVerticalList list = table.add(theme.verticalList()).expandX().widget();

        title(list, setting, true);

        WTable wtable = list.add(theme.table()).expandX().padBottom(settingSpacing()).widget();
        wtable.verticalSpacing = settingSpacing();

        StringListSetting.fillTable(theme, wtable, setting);
    }

    private <T extends Enum<?>> void enumW(WTable table, EnumSetting<T> setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WDropdown<T> dropdown = list.add(theme.dropdown(setting.title, setting.get())).expandCellX().widget();
        dropdown.action = () -> setting.set(dropdown.get());

        reset(table, setting, () -> dropdown.set(setting.get()), () -> list.mouseOver);
    }

    private void providedStringW(WTable table, ProvidedStringSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WDropdown<String> dropdown = list.add(theme.dropdown(setting.title, setting.supplier.get(), setting.get())).expandCellX().widget();
        dropdown.action = () -> setting.set(dropdown.get());

        reset(list, setting, () -> dropdown.set(setting.get()), () -> list.mouseOver);
    }

    private void genericW(WTable table, GenericSetting<?> setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WButton edit = list.add(theme.button(CatpuccinIcons.EDIT.texture())).widget();
        edit.action = () -> mc.setScreen(
                setting/*? if <=1.21.4 >>+ '()' *//*.get()*/.createScreen(theme)
        );

        title(list, setting).padLeft(theme.pad()).expandCellX();
        reset(list, setting, null, () -> list.mouseOver);
    }

    private void colorW(WTable table, ColorSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WColorPicker colorPicker = list.add(theme.colorPicker(setting.get(), CatpuccinIcons.EDIT.texture())).widget();
        colorPicker.action = () -> mc.setScreen(new ColorSettingScreen(theme, setting));

        title(list, setting).padLeft(theme.pad()).expandCellX();

        reset(list, setting, () -> colorPicker.setColor(setting.get()), () -> list.mouseOver);
    }

    private void keybindW(WTable table, KeybindSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).widget();

        WCatpuccinKeybind keybind = list.add(theme.catpuccinKeybind(setting.title, setting.get(), setting.getDefaultValue())).expandX().widget();
        keybind.tooltip = setting.description;
        keybind.action = setting::onChanged;
        setting.widget = keybind;
    }

    private void blockW(WTable table, BlockSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        title(list, setting);

        WItem item = list.add(theme.item(setting.get().asItem().getDefaultStack())).widget();

        WButton select = list.add(theme.button("Select")).right().widget();
        select.minWidth = theme.textWidth(select.getText()) * 2;
        select.action = () -> {
            BlockSettingScreen screen = new BlockSettingScreen(theme, setting);
            screen.onClosed(() -> item.set(setting.get().asItem().getDefaultStack()));

            mc.setScreen(screen);
        };

        reset(list, setting, () -> item.set(setting.get().asItem().getDefaultStack()), () -> list.mouseOver);
    }

    private void blockPosW(WTable table, BlockPosSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        title(list, setting);

        WBlockPosEdit edit = list.add(theme.blockPosEdit(setting.get())).expandX().widget();

        edit.actionOnRelease = () -> {
            if (!setting.set(edit.get())) edit.set(setting.get());
        };

        reset(list, setting, () -> edit.set(setting.get()));
    }

    private void blockListW(WTable table, BlockListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new BlockListSettingScreen(theme, setting)));
    }

    private void itemW(WTable table, ItemSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WItem item = theme.item(setting.get().asItem().getDefaultStack());

        WButton select = list.add(theme.button("Select")).widget();
        select.minWidth = theme.textWidth(select.getText()) * 2;
        select.action = () -> {
            ItemSettingScreen screen = new ItemSettingScreen(theme, setting);
            screen.onClosed(() -> item.set(setting.get().getDefaultStack()));

            mc.setScreen(screen);
        };

        list.add(item);

        title(list, setting);


        reset(list, setting, () -> item.set(setting.get().getDefaultStack()));
    }

    private void itemListW(WTable table, ItemListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new ItemListSettingScreen(theme, setting)));
    }

    private void entityTypeListW(WTable table, EntityTypeListSetting setting) {
        selectW(table, setting, () ->
                mc.setScreen(theme.catpuccinEntityTypeListScreen.get()
                        ? new CatpuccinEntityTypeListSettingScreen(theme, setting)
                        : new EntityTypeListSettingScreen(theme, setting))
        );
    }

    private void enchantmentListW(WTable table, EnchantmentListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new EnchantmentListSettingScreen(theme, setting)));
    }

    private void moduleListW(WTable table, ModuleListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new ModuleListSettingScreen(theme, setting)));
    }

    private void packetListW(WTable table, PacketListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new PacketBoolSettingScreen(theme, setting)));
    }

    private void particleTypeListW(WTable table, ParticleTypeListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new ParticleTypeListSettingScreen(theme, setting)));
    }

    private void soundEventListW(WTable table, SoundEventListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new SoundEventListSettingScreen(theme, setting)));
    }

    private void statusEffectAmplifierMapW(WTable table, StatusEffectAmplifierMapSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new StatusEffectAmplifierMapSettingScreen(theme, setting)));
    }

    private void statusEffectListW(WTable table, StatusEffectListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new StatusEffectListSettingScreen(theme, setting)));
    }

    private void storageBlockListW(WTable table, StorageBlockListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new StorageBlockListSettingScreen(theme, setting)));
    }

    private void screenHandlerListW(WTable table, ScreenHandlerListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new ScreenHandlerSettingScreen(theme, setting)));
    }

    private void blockDataW(WTable table, BlockDataSetting<?> setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WButton button = list.add(theme.button(CatpuccinIcons.EDIT.texture())).widget();
        button.action = () -> mc.setScreen(
                new BlockDataSettingScreen/*? if >= 1.21.11 >>+ '<>'*/<>(theme, setting)
        );

        title(list, setting).padLeft(theme.pad()).expandCellX();
        reset(list, setting, null, () -> list.mouseOver);
    }

    private void potionW(WTable table, PotionSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WItemWithLabel item = theme.itemWithLabel(setting.get().potion, I18n.translate(setting.get().potion.getItem().getTranslationKey()));

        WButton button = list.add(theme.button("Select")).widget();
        button.minWidth = theme.textWidth(button.getText()) * 2;
        button.action = () -> {
            WidgetScreen screen = new PotionSettingScreen(theme, setting);
            screen.onClosed(() -> item.set(setting.get().potion));

            mc.setScreen(screen);
        };

        list.add(item).expandCellX();

        reset(list, setting, () -> item.set(setting.get().potion), () -> list.mouseOver);
    }

    private void fontW(WTable table, FontFaceSetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WCatpuccinLabel label = (WCatpuccinLabel) theme.label(getFontLabel(setting, setting.get().info.family()));

        WButton button = list.add(theme.button("Select")).widget();
        button.minWidth = theme.textWidth(button.getText()) * 2;
        button.action = () -> {
            WidgetScreen screen = new FontFaceSettingScreen(theme, setting);

            screen.onClosed(() -> label.set(getFontLabel(setting, setting.get().info.family())));

            mc.setScreen(screen);
        };

        list.add(label).expandCellX().padLeft(theme.pad());

        reset(list, setting, () -> label.set(getFontLabel(setting, Fonts.DEFAULT_FONT.info.family())), () -> list.mouseOver);
    }

    private RichText getFontLabel(FontFaceSetting setting, String fontFamily) {
        return RichText
            .bold(setting.title)
            .append(" - ")
            .append(fontFamily);
    }

    private void colorListW(WTable table, ColorListSetting setting) {
        WTable tab = table.add(theme.table()).expandX().widget();

        title(tab, setting, true);
        tab.row();

        WTable t = tab.add(theme.table()).expandX().widget();
        tab.row();

        colorListWFill(t, setting);

        WPlus add = tab.add(theme.plus()).expandCellX().widget();
        add.action = () -> {
            setting.get().add(new SettingColor());
            setting.onChanged();

            t.clear();
            colorListWFill(t, setting);
        };

        reset(tab, setting, () -> {
            t.clear();
            colorListWFill(t, setting);
        });
    }

    private void colorListWFill(WTable t, ColorListSetting setting) {
        int i = 0;
        for (SettingColor color : setting.get()) {
            int _i = i;

            WHorizontalList list = t.add(theme.horizontalList()).expandX().widget();

            list.add(theme.label(String.valueOf(_i))).padLeft(theme.pad());

            WColorPicker colorPicker = list.add(theme.colorPicker(color, CatpuccinIcons.EDIT.texture())).padHorizontal(theme.pad()).widget();
            colorPicker.action = () -> {
                SettingColor defaultValue = WHITE;

                if (_i < setting.getDefaultValue().size())
                    defaultValue = setting.getDefaultValue().get(_i);

                ColorSetting set = new ColorSetting(
                    setting.name, setting.description, defaultValue, settingColor -> {
                    setting.get().get(_i).set(settingColor);
                    setting.onChanged();
                }, null, null
                );

                set.set(setting.get().get(_i));
                mc.setScreen(new ColorSettingScreen(theme, set));
            };

            list.add(theme.label(RichText.of("Example Text").scale(TextSize.SMALL.get())).color(color)).expandX();

            WMinus remove = list.add(theme.minus()).right().widget();
            remove.action = () -> {
                setting.get().remove(_i);
                setting.onChanged();

                t.clear();
                colorListWFill(t, setting);
            };

            t.row();
            i++;
        }
    }

    private void vector3dW(WTable table, Vector3dSetting setting) {
        WVerticalList list = table.add(theme.verticalList()).expandX().widget();
        WHorizontalList horizontalList = list.add(theme.horizontalList()).expandX().widget();

        title(horizontalList, setting, true);

        WTable internal = list.add(theme.table()).expandX().widget();

        WCatpuccinDoubleEdit x = addVectorComponent(internal, "X", setting.get().x, val -> setting.get().x = val, setting);
        WCatpuccinDoubleEdit y = addVectorComponent(internal, "Y", setting.get().y, val -> setting.get().y = val, setting);
        WCatpuccinDoubleEdit z = addVectorComponent(internal, "Z", setting.get().z, val -> setting.get().z = val, setting);

        reset(horizontalList, setting, () -> {
            x.set(setting.get().x);
            y.set(setting.get().y);
            z.set(setting.get().z);
        });
    }

    private WCatpuccinDoubleEdit addVectorComponent(WTable table, String label, double value, Consumer<Double> update, Vector3dSetting setting) {
        WCatpuccinDoubleEdit component = table.add(theme.catpuccinDoubleEdit(label, setting.description, value, setting.min, setting.max, setting.sliderMin, setting.sliderMax, setting.decimalPlaces, setting.noSlider)).expandX().widget();
        if (setting.onSliderRelease) {
            component.actionOnRelease = () -> update.accept(component.get());
        } else {
            component.action = () -> update.accept(component.get());
        }

        table.row();

        return component;
    }

    // Other

    private void selectW(WContainer c, Setting<?> setting, Runnable action) {
        boolean addCount = WSelectedCountLabel.getSize(setting) != -1;

        WHorizontalList list = c.add(theme.horizontalList()).expandX().widget();

        WButton button = list.add(theme.button("Select")).widget();
        button.minWidth = theme.textWidth(button.getText()) * 2;
        button.action = action;

        title(list, setting).padLeft(theme.pad());

        if (addCount) list.add(new WSelectedCountLabel(setting).color(theme.accentColor())).expandCellX();

        reset(list, setting, null, () -> list.mouseOver);
    }

    private Cell<WCatpuccinButton> reset(WContainer c, Setting<?> setting, Runnable action) {
        return reset(c, setting, action, null);
    }

    private Cell<WCatpuccinButton> reset(WContainer c, Setting<?> setting, Runnable action, BooleanSupplier visibilityCondition) {
        WCatpuccinButton button = (WCatpuccinButton) theme.button(CatpuccinIcons.RESET.texture());

        button.setVisibilityCondition(visibilityCondition);
        button.tooltip = "Reset";
        button.action = () -> {
            setting.reset();
            if (action != null) action.run();
        };

        return c.add(button).right();
    }

    private Cell<WLabel> title(WContainer c, Setting<?> setting) {
        return title(c, setting, false);
    }

    private Cell<WLabel> title(WContainer c, Setting<?> setting, boolean bold) {
        Cell<WLabel> title = c.add(theme.label(RichText.of(setting.title).boldIf(bold)));
        title.widget().tooltip = setting.description;

        return title;
    }

    private static class WSelectedCountLabel extends WCatpuccinLabel {
        private final Setting<?> setting;
        private int lastSize = -1;
        private double offsetX;
        private double offsetY;

        public WSelectedCountLabel(Setting<?> setting) {
            super(RichText.of(""));
            this.setting = setting;
        }

        @Override
        protected void onCalculateSize() {
            super.onCalculateSize();

            double pad = theme().pad();
            width = pad + theme().textWidth(richText) + pad;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();
            int size = getSize(setting);

            if (size != lastSize) {
                set(RichText.of(String.valueOf(size)));
                onCalculateSize();

                offsetX = width / 2 - theme.textWidth(richText) / 2;
                offsetY = height / 2 - theme.textHeight() / 2;
                lastSize = size;
            }

            renderer().roundedRect(
                    this,
                    radius(),
                    theme().surface0Color(),
                    CornerStyle.ALL
            );

            renderer().text(
                    richText,
                    x + offsetX,
                    y + offsetY,
                    color != null ? color : theme().textColor()
            );
        }

        public static int getSize(Setting<?> setting) {
            if (setting.get() instanceof Collection<?> collection) return collection.size();
            if (setting.get() instanceof Map<?, ?> map) return map.size();

            return -1;
        }
    }
}
