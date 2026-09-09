package me.pindour.catppuccin.gui.screens.settings;

import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.ColorLink;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.ColorLinkRegistry;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinLabel;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinQuad;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinColorGrid;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinColorSlider;
import me.pindour.catppuccin.gui.widgets.tabs.CatppuccinTab;
import me.pindour.catppuccin.gui.widgets.tabs.TabBuilder;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

//? if <=1.21.1
//import net.minecraft.nbt.CompoundTag;

public class CatppuccinColorSettingScreen extends WindowScreen {
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color[] HUE_COLORS = {
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(0, 0, 255),
            new Color(255, 0, 255),
            new Color(255, 0, 0)
    };

    private final CatppuccinGuiTheme theme;
    private final ColorSetting setting;

    private WCatppuccinQuad displayQuad;

    private WCatppuccinColorGrid<CatppuccinColor> linkGrid;
    private WCatppuccinColorGrid<CatppuccinColor> paletteGrid;

    private WCatppuccinColorSlider hueSlider;
    private WCatppuccinColorSlider saturationSlider;
    private WCatppuccinColorSlider valueSlider;
    private WCatppuccinColorSlider opacitySlider;
    private WCatppuccinColorSlider linkOpacitySlider;

    private Color[] saturationColors;
    private Color[] valueColors;
    private Color[] opacityColors;
    private Color[] linkOpacityColors;

    public CatppuccinColorSettingScreen(GuiTheme theme, ColorSetting setting) {
        super(theme, setting.title);
        this.theme = (CatppuccinGuiTheme) theme;
        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        List<CatppuccinTab> tabs = new TabBuilder()
                .tab("Custom color", this::buildCustomTab)
                .tab("Link color", this::buildLinkTab)
                .build();

        CatppuccinTab initialTab = ColorLinkRegistry.isLinked(setting) ? tabs.get(1) : tabs.get(0);

        add(theme.tabView(tabs, initialTab)).centerX();

        // Reload the parent screen to refresh the "linked" icons
        // for the Color Setting widgets
        onClosed(() -> {
            if (parent instanceof WidgetScreen p) p.reload();
        });
    }

    // Custom color tab

    private void buildCustomTab(WContainer content) {
        WVerticalList list = tabContentList(content);

        buildPreview(list);
        buildHsvSliders(list);
        buildCopyPasteButtons(list);
        buildPaletteGrid(list);
        buildBackButton(list);
    }

    private void buildHsvSliders(WVerticalList list) {
        WVerticalList sliderList = list.add(theme.verticalList()).expandX().widget();
        sliderList.spacing = theme.pad();

        // Hue
        hueSlider = theme.colorSlider(0, 0, 360, HUE_COLORS);
        addColorSlider(sliderList, "Hue", "°", hueSlider, this::hueChanged);
        hueSlider.set(ColorUtils.hueFromColor(setting.get()));

        // Saturation
        saturationColors = new Color[] {
                WHITE, ColorUtils.hueToColor(hueSlider.get())
        };
        saturationSlider = theme.colorSlider(0, 0, 100, saturationColors);
        addColorSlider(sliderList, "Saturation", "%", saturationSlider, this::hsvChanged);

        // Value
        valueColors = new Color[] {
                BLACK, saturationColors[1]
        };
        valueSlider = theme.colorSlider(0, 0, 100, valueColors);
        addColorSlider(sliderList, "Value", "%", valueSlider, this::hsvChanged);

        saturationValueFromColor(setting.get());

        // Opacity
        SettingColor c = setting.get();
        opacityColors = opacityGradient(c);
        opacitySlider = theme.colorSlider(ColorUtils.alphaToPercent(c.a), 0, 100, opacityColors);
        addColorSlider(sliderList, "Opacity", "%", opacitySlider, this::opacityChanged);
    }

    private void buildCopyPasteButtons(WVerticalList list) {
        WHorizontalList copyPasteList = list.add(theme.horizontalList()).expandX().widget();
        copyPasteList.spacing = theme.scale(6);

        WButton copyButton = copyPasteList.add(theme.button("Copy", CatppuccinBuiltinIcons.COPY.texture())).expandX().widget();
        copyButton.action = this::toClipboard;
        copyButton.tooltip = "Copy config";

        WButton pasteButton = copyPasteList.add(theme.button("Paste", CatppuccinBuiltinIcons.IMPORT.texture())).expandX().widget();
        pasteButton.action = this::fromClipboard;
        pasteButton.tooltip = "Paste config";
    }

    private void buildPaletteGrid(WVerticalList list) {
        paletteGrid = list.add(theme.colorGrid(
                "Palette",
                CatppuccinColor.values(),
                theme::getColor,
                6,
                2
        ))
        .expandX()
        .widget();

        paletteGrid.onColorSelected = paletteColor -> {
            ColorLinkRegistry.unlink(setting);
            setting.get().set(theme.getColor(paletteColor));
            setting.onChanged();

            setFromSetting();
        };

        paletteGrid.selectMatching(setting.get());
    }

    // Link color tab

    private void buildLinkTab(WContainer content) {
        WVerticalList list = tabContentList(content);

        buildPreview(list);
        buildLinkDescription(list);
        buildLinkGrid(list);
        buildLinkOpacitySlider(list);
        buildBackButton(list);
    }

    private void buildLinkDescription(WVerticalList list) {
        WLabel label = list.add(theme.label(
                "Linking this color will keep it synchronized with the theme, " +
                "automatically updating when you change your flavor or accent color.",
                theme.scale(240)
        ))
        .expandX()
        .widget();

        label.color = theme.textSecondaryColor();
    }

    private void buildLinkGrid(WVerticalList list) {
        linkGrid = list.add(theme.colorGrid(
                "Link with palette",
                CatppuccinColor.values(),
                theme::getColor,
                6,
                3
        ))
        .expandX()
        .widget();

        linkGrid.setExpanded(true);
        linkGrid.onColorSelected = paletteColor -> {
            ColorLinkRegistry.link(setting, paletteColor);
            setFromSetting();
        };

        ColorLink link = ColorLinkRegistry.getLink(setting);
        if (link != null) linkGrid.select(link.color());
    }

    private void buildLinkOpacitySlider(WVerticalList list) {
        ColorLink link = ColorLinkRegistry.getLink(setting);
        int opacity = link != null ? link.opacity() : 255;

        SettingColor c = setting.get();
        linkOpacityColors = opacityGradient(c);
        linkOpacitySlider = theme.colorSlider(ColorUtils.alphaToPercent(opacity), 0, 100, linkOpacityColors);
        addColorSlider(list, "Opacity", "%", linkOpacitySlider, this::linkOpacityChanged);
    }

    private WVerticalList tabContentList(WContainer content) {
        WVerticalList list = content.add(theme.verticalList())
                .padHorizontal(theme.scale(12))
                .padVertical(theme.scale(8))
                .expandX()
                .widget();

        list.spacing = theme.scale(16);
        return list;
    }

    private void buildPreview(WVerticalList list) {
        WVerticalList previewList = list.add(theme.verticalList()).expandX().widget();
        previewList.spacing = theme.pad();

        WHorizontalList previewHeader = previewList.add(theme.horizontalList()).expandX().widget();
        previewHeader.add(theme.label("Preview")).expandX();

        WButton resetButton = previewHeader.add(theme.button(CatppuccinBuiltinIcons.RESET.texture())).widget();
        resetButton.action = () -> {
            setting.reset();
            setFromSetting();
        };
        resetButton.tooltip = "Reset";

        displayQuad = (WCatppuccinQuad) previewList.add(theme.quad(setting.get())).expandX().widget();
        displayQuad.radius = theme.cornerRadius.get();
        displayQuad.corners = Corners.ALL;
        displayQuad.minHeight = theme.scale(42);
    }

    private void buildBackButton(WVerticalList list) {
        WButton backButton = list.add(theme.button("Back")).expandX().widget();
        backButton.action = this::onClose;
    }

    private void addColorSlider(WVerticalList list, String title, String suffix, WCatppuccinColorSlider slider, Runnable onValueChanged) {
        WSliderContainer container = list.add(new WSliderContainer(slider, title, suffix)).expandX().widget();

        slider.action = () -> {
            onValueChanged.run();
            container.updateValueText();
        };
    }

    private void setFromSetting() {
        if (displayQuad == null) return;

        SettingColor c = setting.get();

        displayQuad.color.set(c);

        if (linkGrid != null) syncLinkTab(c);

        if (hueSlider != null) {
            syncCustomTab(c);
            if (paletteGrid != null) paletteGrid.selectMatching(setting.get());
        }
    }

    // Color state sync

    private void syncCustomTab(SettingColor c) {
        hueSlider.set(ColorUtils.hueFromColor(c));

        updateSliderGradients();
        saturationValueFromColor(c);

        opacityColors[1] = ColorUtils.withAlpha(c, 255);
        opacitySlider.set(ColorUtils.alphaToPercent(c.a));
    }

    private void syncLinkTab(SettingColor c) {
        ColorLink link = ColorLinkRegistry.getLink(setting);

        linkGrid.select(link != null ? link.color() : null);

        linkOpacityColors[1] = ColorUtils.withAlpha(c, 255);
        linkOpacitySlider.set(ColorUtils.alphaToPercent(link != null ? link.opacity() : 255));
    }

    private void hueChanged() {
        updateSliderGradients();
        hsvChanged();
    }

    private void hsvChanged() {
        Color c = setting.get();
        ColorUtils.hsvToRgb(c, hueSlider.get(), saturationSlider.get() / 100.0, valueSlider.get() / 100.0);
        applyEditedColor();
    }

    private void opacityChanged() {
        setting.get().a = ColorUtils.percentToAlpha(opacitySlider.get());
        applyEditedColor();
    }

    private void linkOpacityChanged() {
        ColorLink link = ColorLinkRegistry.getLink(setting);
        if (link == null) return;

        link.setOpacity(ColorUtils.percentToAlpha(linkOpacitySlider.get()));
        link.apply(setting);
        applyColor(setting.get(), linkOpacityColors);
    }

    private void applyEditedColor() {
        ColorLinkRegistry.unlink(setting);
        applyColor(setting.get(), opacityColors);
    }

    private void applyColor(SettingColor c, Color[] gradientColors) {
        c.validate();

        gradientColors[1] = ColorUtils.withAlpha(c, 255);

        displayQuad.color.set(c);
        setting.onChanged();
    }

    private void updateSliderGradients() {
        Color hueColor = ColorUtils.hueToColor(hueSlider.get());
        saturationColors[1] = hueColor;
        valueColors[1] = hueColor;
    }

    private void saturationValueFromColor(Color c) {
        saturationSlider.set(ColorUtils.saturationFromColor(c) * 100);
        valueSlider.set(ColorUtils.valueFromColor(c) * 100);
    }

    private Color[] opacityGradient(SettingColor c) {
        return new Color[] {
                ColorUtils.brighter(theme.baseColor()), ColorUtils.withAlpha(c, 255)
        };
    }

    @Override
    public boolean toClipboard() {
        //? if <=1.21.1 {
        /*return NbtUtils.toClipboard(setting.name, setting.get().toTag());
        *///? } else {
        return NbtUtils.toClipboard(setting.get());
        //? }
    }

    @Override
    public boolean fromClipboard() {
        //? if <=1.21.1 {
        /*CompoundTag clipboardTag = NbtUtils.fromClipboard(setting.get().toTag());

        if (clipboardTag == null) {
            String clipboard = mc.keyboardHandler.getClipboard().trim();

            SettingColor parsed = ColorUtils.parseRGBA(clipboard);
            if (parsed == null) parsed = ColorUtils.parseHex(clipboard);
            if (parsed == null) return false;

            setting.set(parsed);
        } else {
            setting.get().fromTag(clipboardTag);
        }
        *///? } else {
        if (!NbtUtils.fromClipboard(setting.get())) {
            String clipboard = mc.keyboardHandler.getClipboard().trim();

            SettingColor parsed = ColorUtils.parseRGBA(clipboard);
            if (parsed == null) parsed = ColorUtils.parseHex(clipboard);
            if (parsed == null) return false;

            setting.set(parsed);
        }
        //? }

        ColorLinkRegistry.unlink(setting);
        setting.get().validate();

        if (parent instanceof WidgetScreen p) p.reload();
        reload();

        return true;
    }

    private static class WSliderContainer extends WVerticalList {
        private final WCatppuccinColorSlider slider;
        private final String title;
        private final String valueSuffix;

        private WCatppuccinLabel valueLabel;

        public WSliderContainer(WCatppuccinColorSlider slider, String title, String valueSuffix) {
            this.slider = slider;
            this.title = title;
            this.valueSuffix = valueSuffix;
        }

        @Override
        public void init() {
            spacing = 0;

            WHorizontalList titleList = add(theme.horizontalList()).expandX().widget();

            titleList.add(theme.label(title));
            valueLabel = (WCatppuccinLabel) titleList.add(theme.label(getValueFormatted())).expandX().right().widget();
            valueLabel.textRight();

            add(slider).expandX();
        }

        private String getValueFormatted() {
            return (int) slider.get() + valueSuffix;
        }

        public void updateValueText() {
            valueLabel.set(RichText.of(getValueFormatted()));
        }
    }
}
