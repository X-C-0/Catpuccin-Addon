package me.pindour.catppuccin.gui.themes.catppuccin;

import me.pindour.catppuccin.gui.animation.Easing;
import me.pindour.catppuccin.gui.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.gui.screens.CatppuccinModuleScreen;
import me.pindour.catppuccin.gui.screens.CatppuccinModulesScreen;
import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.text.RichTextRenderer;
import me.pindour.catppuccin.gui.text.RichTextSegment;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinAccentColor;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.CatppuccinFlavors;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinSection;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinView;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinWindow;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinDropdown;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinMultiSelect;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinSlider;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinTextBox;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.settings.WCatppuccinDoubleEdit;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.settings.WCatppuccinIntEdit;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.settings.WCatppuccinKeybind;
import me.pindour.catppuccin.gui.widgets.WGuiTexture;
import me.pindour.catppuccin.gui.widgets.input.WMultiSelect;
import me.pindour.catppuccin.gui.widgets.pressable.WColorPicker;
import me.pindour.catppuccin.gui.widgets.pressable.WOpenIndicator;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.*;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.*;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.gui.screen.Screen;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static meteordevelopment.meteorclient.MeteorClient.mc;

//? if <1.21.9
//import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;
//? if >=1.21.10
import net.minecraft.client.util.MacWindowUtil;

public class CatppuccinGuiTheme extends GuiTheme {
    private final Map<CatppuccinColor, Color> colorCache;

    private RichTextRenderer textRenderer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgCorners = settings.createGroup("Corners");
    private final SettingGroup sgAnimations = settings.createGroup("Animations");
    private final SettingGroup sgColors = settings.createGroup("Colors");
    private final SettingGroup sgSnapping = settings.createGroup("Snapping");
    private final SettingGroup sgScreens = settings.createGroup("Screens");
    private final SettingGroup sgStarscript = settings.createGroup("Starscript");

    // General

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of the GUI.")
            .defaultValue(1)
            .min(0.75)
            .sliderRange(0.75, 4)
            .onSliderRelease()
            .onChanged(aDouble -> {
                if (mc.currentScreen instanceof WidgetScreen) ((WidgetScreen) mc.currentScreen).invalidate();
            })
            .build()
    );

    public final Setting<AlignmentX> moduleAlignment = sgGeneral.add(new EnumSetting.Builder<AlignmentX>()
            .name("module-alignment")
            .description("How module titles are aligned.")
            .defaultValue(AlignmentX.Center)
            .build()
    );

    public final Setting<Boolean> categoryIcons = sgGeneral.add(new BoolSetting.Builder()
            .name("category-icons")
            .description("Displays icons next to module categories.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> hideHUD = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-HUD")
            .description("Hide HUD when in GUI.")
            .defaultValue(false)
            .onChanged(v -> {
                if (mc.currentScreen instanceof WidgetScreen) mc.options.hudHidden = v;
            })
            .build()
    );

    public final Setting<Boolean> widgetOutline = sgGeneral.add(new BoolSetting.Builder()
            .name("widget-outline")
            .description("Renders a subtle outline around UI elements.")
            .defaultValue(true)
            .build()
    );

    // Corners

    public final Setting<Boolean> roundedCorners = sgCorners.add(new BoolSetting.Builder()
            .name("rounded-corners")
            .description("Toggles rounded corners on UI elements.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Integer> cornerRadius = sgCorners.add(new IntSetting.Builder()
            .name("corner-radius")
            .description("The radius of corners for large UI elements.")
            .defaultValue(10)
            .sliderRange(1, 25)
            .visible(roundedCorners::get)
            .build()
    );

    public final Setting<Integer> smallCornerRadius = sgCorners.add(new IntSetting.Builder()
            .name("small-corner-radius")
            .description("The radius of corners for small UI elements.")
            .defaultValue(6)
            .sliderRange(1, 25)
            .visible(roundedCorners::get)
            .build()
    );

    // Animations

    public final Setting<Easing> guiAnimation = sgAnimations.add(new EnumSetting.Builder<Easing>()
            .name("gui-animation-easing")
            .description("The easing function used for UI animations.")
            .defaultValue(Easing.QUART_OUT)
            .build()
    );

    public final Setting<Integer> guiAnimationDuration = sgAnimations.add(new IntSetting.Builder()
            .name("gui-animation-duration")
            .description("Duration of the animation in milliseconds.")
            .defaultValue(300)
            .sliderRange(1, 1000)
            .build()
    );

    // Colors

    public final Setting<CatppuccinFlavors> flavor = sgColors.add(new EnumSetting.Builder<CatppuccinFlavors>()
            .name("Flavor")
            .description("The specific Catppuccin flavor (palette) to use.")
            .defaultValue(CatppuccinFlavors.Macchiato)
            .onChanged(this::updateCache)
            .build()
    );

    private final Setting<CatppuccinAccentColor> accentColor = sgColors.add(new EnumSetting.Builder<CatppuccinAccentColor>()
            .name("Accent")
            .description("The main accent color used throughout the UI.")
            .defaultValue(CatppuccinAccentColor.Mauve)
            .build()
    );

    public final Setting<Double> windowOpacity = sgColors.add(new DoubleSetting.Builder()
            .name("window-opacity")
            .description("Controls the opacity of the windows.")
            .defaultValue(1)
            .sliderRange(0, 1)
            .decimalPlaces(2)
            .build()
    );

    public final Setting<Double> backgroundOpacity = sgColors.add(new DoubleSetting.Builder()
            .name("background-opacity")
            .description("Controls the opacity of the backgrounds of UI elements.")
            .defaultValue(1)
            .sliderRange(0, 1)
            .decimalPlaces(2)
            .build()
    );

    // Snapping

    public final Setting<Boolean> snapModuleCategories = sgSnapping.add(new BoolSetting.Builder()
            .name("snap-module-categories")
            .description("Snaps category windows to the grid.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Integer> snappingGridSize = sgSnapping.add(new IntSetting.Builder()
            .name("grid-size")
            .description("The size of the snapping grid.")
            .defaultValue(10)
            .sliderRange(5, 50)
            .build()
    );

    // Screens

    public final Setting<Boolean> catppuccinEntityTypeListScreen = sgScreens.add(new BoolSetting.Builder()
            .name("entity-type-list-screen")
            .description("Replaces Meteor's entity selection screen with the Catppuccin version.")
            .defaultValue(true)
            .build()
    );

    // Three state colors

    public final ThreeStateColor backgroundColor = new ThreeStateColor(
            this::surface0Color,
            this::surface1Color,
            this::surface2Color
    );

    public final ThreeStateColor outlineColor = new ThreeStateColor(
            this::overlay0Color,
            this::overlay1Color,
            this::overlay2Color
    );

    public final ThreeStateColor scrollbarColor = new ThreeStateColor(
            this::surface0Color,
            this::surface1Color,
            this::surface2Color
    );

    // Starscript

    private final Setting<SettingColor> starscriptText = color(sgStarscript, "starscript-text", "Color of text in Starscript code.", new SettingColor(169, 183, 198));
    private final Setting<SettingColor> starscriptBraces = color(sgStarscript, "starscript-braces", "Color of braces in Starscript code.", new SettingColor(150, 150, 150));
    private final Setting<SettingColor> starscriptParenthesis = color(sgStarscript, "starscript-parenthesis", "Color of parenthesis in Starscript code.", new SettingColor(169, 183, 198));
    private final Setting<SettingColor> starscriptDots = color(sgStarscript, "starscript-dots", "Color of dots in starscript code.", new SettingColor(169, 183, 198));
    private final Setting<SettingColor> starscriptCommas = color(sgStarscript, "starscript-commas", "Color of commas in starscript code.", new SettingColor(169, 183, 198));
    private final Setting<SettingColor> starscriptOperators = color(sgStarscript, "starscript-operators", "Color of operators in Starscript code.", new SettingColor(169, 183, 198));
    private final Setting<SettingColor> starscriptStrings = color(sgStarscript, "starscript-strings", "Color of strings in Starscript code.", new SettingColor(106, 135, 89));
    private final Setting<SettingColor> starscriptNumbers = color(sgStarscript, "starscript-numbers", "Color of numbers in Starscript code.", new SettingColor(104, 141, 187));
    private final Setting<SettingColor> starscriptKeywords = color(sgStarscript, "starscript-keywords", "Color of keywords in Starscript code.", new SettingColor(204, 120, 50));
    private final Setting<SettingColor> starscriptAccessedObjects = color(sgStarscript, "starscript-accessed-objects", "Color of accessed objects (before a dot) in Starscript code.", new SettingColor(152, 118, 170));

    public CatppuccinGuiTheme() {
        super("Catppuccin");

        settingsFactory = new CatppuccinSettingsWidgetFactory(this);
        colorCache = new EnumMap<>(CatppuccinColor.class);

        updateCache(flavor.get());
    }

    private Setting<SettingColor> color(SettingGroup group, String name, String description, SettingColor color) {
        return group.add(new ColorSetting.Builder()
                .name(name + "-color")
                .description(description)
                .defaultValue(color)
                .build());
    }

    // Widgets

    @Override
    public WWindow window(WWidget icon, String title) {
        return w(new WCatppuccinWindow(icon, title));
    }

    public WLabel label(RichText text, double maxWidth) {
        if (maxWidth == 0) return w(new WCatppuccinLabel(text));
        return w(new WCatppuccinMultiLabel(text, maxWidth));
    }

    public WLabel label(RichText text) {
        return label(text, 0);
    }

    @Override
    public WLabel label(String text, boolean title, double maxWidth) {
        if (maxWidth == 0) return w(new WCatppuccinLabel(RichText.of(text).boldIf(title)));
        return w(new WCatppuccinMultiLabel(RichText.of(text).boldIf(title), maxWidth));
    }

    @Override
    public WHorizontalSeparator horizontalSeparator(String text) {
        return w(new WCatppuccinHorizontalSeparator(text));
    }

    @Override
    public WVerticalSeparator verticalSeparator() {
        return w(new WCatppuccinVerticalSeparator());
    }

    public WCatppuccinButton button(RichText text, GuiTexture texture) {
        return w(new WCatppuccinButton(text, texture));
    }

    public WCatppuccinButton button(RichText text) {
        return button(text, null);
    }

    @Override
    public WButton button(String text, GuiTexture texture) {
        return button(RichText.of(text), texture);
    }

    @Override
    public WButton button(GuiTexture texture) {
        return w(new WCatppuccinButton(texture));
    }

    //? if >=1.21.11 {
    @Override
    protected WConfirmedButton confirmedButton(String text, String confirmText, GuiTexture texture) {
        return w(new WCatppuccinConfirmedButton(text, confirmText, texture));
    }
    //? }

    @Override
    public WMinus minus() {
        return w(new WCatppuccinMinus());
    }

    //? if >=1.21.11 {
    @Override
    public WConfirmedMinus confirmedMinus() {
        return w(new WCatppuccinConfirmedMinus());
    }
    //? }

    @Override
    public WPlus plus() {
        return w(new WCatppuccinPlus());
    }

    @Override
    public WCheckbox checkbox(boolean checked) {
        return w(new WCatppuccinCheckbox(checked));
    }

    @Override
    public WSlider slider(double value, double min, double max) {
        return w(new WCatppuccinSlider(value, min, max));
    }

    public WTextBox textBox(String text, String placeholder, String title, double padding, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
        return w(new WCatppuccinTextBox(text, placeholder, title, padding, filter, renderer));
    }

    public WTextBox textBox(String text, String placeholder, String title, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
        return textBox(text, placeholder, title, pad(), filter, renderer);
    }

    public WTextBox textBox(String text, CharFilter filter, double padding) {
        return textBox(text, null, "", padding, filter, null);
    }

    @Override
    public WTextBox textBox(String text, String placeholder, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
        return textBox(text, placeholder, "", filter, renderer);
    }

    public <T> WDropdown<T> dropdown(String title, T[] values, T value) {
        return w(new WCatppuccinDropdown<>(title, values, value));
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> WDropdown<T> dropdown(String title, T value) {
        Class<?> klass = value.getDeclaringClass();
        T[] values = (T[]) klass.getEnumConstants();
        return dropdown(title, values, value);
    }

    @Override
    public <T> WDropdown<T> dropdown(T[] values, T value) {
        return dropdown(null, values, value);
    }

    @Override
    public WTriangle triangle() {
        return w(new WCatppuccinTriangle());
    }

    @Override
    public WTooltip tooltip(String text) {
        return w(new WCatppuccinTooltip(text));
    }

    @Override
    public WView view() {
        return w(new WCatppuccinView());
    }

    @Override
    public WSection section(String title, boolean expanded, WWidget headerWidget) {
        return w(new WCatppuccinSection(title, expanded, headerWidget));
    }

    @Override
    public WAccount account(WidgetScreen screen, Account<?> account) {
        return w(new WCatppuccinAccount(screen, account));
    }

    @Override
    public WWidget module(Module module) {
        return w(module(module, module.title));
    }

    //? if >= 1.21.11
    @Override
    public WWidget module(Module module, String title) {
        return w(new WCatppuccinModule(module, title));
    }

    @Override
    public WQuad quad(Color color) {
        return w(new WCatppuccinQuad(color));
    }

    @Override
    public WTopBar topBar() {
        return w(new WCatppuccinTopBar());
    }

    @Override
    public WFavorite favorite(boolean checked) {
        return w(new WCatppuccinFavorite(checked));
    }

    public WCatppuccinKeybind catppuccinKeybind(Keybind keybind) {
        return catppuccinKeybind(keybind, Keybind.none());
    }

    public WCatppuccinKeybind catppuccinKeybind(Keybind keybind, Keybind defaultValue) {
        return catppuccinKeybind(null, keybind, defaultValue);
    }

    public WCatppuccinKeybind catppuccinKeybind(String title, Keybind keybind, Keybind defaultValue) {
        return w(new WCatppuccinKeybind(title, keybind, defaultValue));
    }

    public WOpenIndicator openIndicator(boolean open) {
        return w(new WCatppuccinOpenIndicator(open));
    }

    public WGuiTexture texture(GuiTexture texture, double size) {
        return w(new WCatppuccinGuiTexture(texture, size));
    }

    public WColorPicker colorPicker(Color color, GuiTexture overlayTexture) {
        return w(new WCatppuccinColorPicker(color, overlayTexture));
    }

    public <T> WMultiSelect<T> multiSelect(String title, List<T> items) {
        return w(new WCatppuccinMultiSelect<>(title, items));
    }

    // Settings widgets

    public WCatppuccinIntEdit catppuccinIntEdit(IntSetting setting) {
        return w(new WCatppuccinIntEdit(setting));
    }

    public WCatppuccinDoubleEdit catppuccinDoubleEdit(String title, String description, double value, double min, double max, int decimalPlaces, double sliderMin, double sliderMax, boolean noSlider) {
        return w(new WCatppuccinDoubleEdit(title, description, null, value, min, max, decimalPlaces, sliderMin, sliderMax, noSlider));
    }

    public WCatppuccinDoubleEdit catppuccinDoubleEdit(DoubleSetting setting) {
        return w(new WCatppuccinDoubleEdit(setting));
    }

    // Animations

    public Easing guiAnimationEasing() {
        return guiAnimation.get();
    }

    public int guiAnimationDuration() {
        return guiAnimationDuration.get();
    }

    // Colors - Accent

    public Color accentColor() {
        return colorCache.get(accentColor.get().toColor());
    }

    // Colors - Main

    public Color greenColor() {
        return colorCache.get(CatppuccinColor.Green);
    }

    public Color yellowColor() {
        return colorCache.get(CatppuccinColor.Yellow);
    }

    public Color redColor() {
        return colorCache.get(CatppuccinColor.Red);
    }

    public Color blueColor() {
        return colorCache.get(CatppuccinColor.Blue);
    }

    // Colors - Overlay

    public Color overlay2Color() {
        return colorCache.get(CatppuccinColor.Overlay2);
    }

    public Color overlay1Color() {
        return colorCache.get(CatppuccinColor.Overlay1);
    }

    public Color overlay0Color() {
        return colorCache.get(CatppuccinColor.Overlay0);
    }

    // Colors - Surface

    public Color surface2Color() {
        return colorCache.get(CatppuccinColor.Surface2);
    }

    public Color surface1Color() {
        return colorCache.get(CatppuccinColor.Surface1);
    }

    public Color surface0Color() {
        return colorCache.get(CatppuccinColor.Surface0);
    }

    // Colors - Base

    public Color baseColor() {
        return colorCache.get(CatppuccinColor.Base);
    }

    public Color mantleColor() {
        return colorCache.get(CatppuccinColor.Mantle);
    }

    public Color crustColor() {
        return colorCache.get(CatppuccinColor.Crust);
    }

    // Colors - Text

    public Color textColor() {
        return colorCache.get(CatppuccinColor.Text);
    }

    public Color textSecondaryColor() {
        return colorCache.get(CatppuccinColor.Subtext1);
    }

    public Color textHighlightColor() {
        return colorCache.get(CatppuccinColor.Blue);
    }

    // Opacity

    public double windowOpacity() {
        return windowOpacity.get();
    }

    public double backgroundOpacity() {
        return backgroundOpacity.get();
    }

    // Starscript

    @Override
    public Color starscriptTextColor() {
        return starscriptText.get();
    }

    @Override
    public Color starscriptBraceColor() {
        return starscriptBraces.get();
    }

    @Override
    public Color starscriptParenthesisColor() {
        return starscriptParenthesis.get();
    }

    @Override
    public Color starscriptDotColor() {
        return starscriptDots.get();
    }

    @Override
    public Color starscriptCommaColor() {
        return starscriptCommas.get();
    }

    @Override
    public Color starscriptOperatorColor() {
        return starscriptOperators.get();
    }

    @Override
    public Color starscriptStringColor() {
        return starscriptStrings.get();
    }

    @Override
    public Color starscriptNumberColor() {
        return starscriptNumbers.get();
    }

    @Override
    public Color starscriptKeywordColor() {
        return starscriptKeywords.get();
    }

    @Override
    public Color starscriptAccessedObjectColor() {
        return starscriptAccessedObjects.get();
    }

    // Colors - Other

    private void updateCache(CatppuccinFlavors newFlavor) {
        colorCache.clear();

        for (CatppuccinColor color : CatppuccinColor.values()) {
            SettingColor settingColor = newFlavor.getColor(color);
            colorCache.put(color, settingColor);
        }
    }

    // Screens

    @Override
    public TabScreen modulesScreen() {
        return new CatppuccinModulesScreen(this);
    }

    @Override
    public boolean isModulesScreen(Screen screen) {
        return screen instanceof CatppuccinModulesScreen;
    }

    @Override
    public WidgetScreen moduleScreen(Module module) {
        return new CatppuccinModuleScreen(this, module);
    }

    // Text renderer

    @Override
    public TextRenderer textRenderer() {
        return richTextRenderer();
    }

    public RichTextRenderer richTextRenderer() {
        if (textRenderer == null)
            setTextRenderer(new RichTextRenderer(Config.get().font.get()));

        return textRenderer;
    }

    public void setTextRenderer(RichTextRenderer renderer) {
        if (textRenderer != null) textRenderer.destroy();
        this.textRenderer = renderer;
    }

    // Text

    public double textWidth(RichTextSegment segment) {
        return scale(richTextRenderer().getWidth(segment, segment.getText().length()));
    }

    public double textWidth(RichText text) {
        return scale(richTextRenderer().getWidth(text));
    }

    @Override
    public double textWidth(String text, int length, boolean title) {
        return scale(richTextRenderer().getWidth(RichText.of(text).boldIf(title), length));
    }

    @Override
    public double textWidth(String text) {
        return textWidth(RichText.of(text));
    }

    public double textHeight(RichText text) {
        return scale(richTextRenderer().getHeight(text));
    }

    @Override
    public double textHeight(boolean title) {
        return scale(richTextRenderer().getHeight(title));
    }

    @Override
    public double textHeight() {
        return textHeight(false);
    }

    // Other

    @Override
    public void beforeRender() {
        super.beforeRender();
        CatppuccinRenderer.get().setTheme(this);
    }

    @Override
    public double scale(double value) {
        double scaled = value * scale.get();

        if (//? if >=1.21.9
            MacWindowUtil.IS_MAC
            //? if <1.21.9
            //IS_SYSTEM_MAC
        ) {
            scaled /= (double) mc.getWindow().getWidth() / mc.getWindow().getFramebufferWidth();
        }

        return scaled;
    }

    @Override
    public boolean categoryIcons() {
        return categoryIcons.get();
    }

    @Override
    public boolean hideHUD() {
        return hideHUD.get();
    }

    public class ThreeStateColor {
        private final Supplier<Color> normal, hovered, pressed;

        public ThreeStateColor(Supplier<Color> normal, Supplier<Color> hovered, Supplier<Color> pressed) {
            this.normal = normal;
            this.hovered = hovered;
            this.pressed = pressed;
        }

        public Color get() {
            return normal.get();
        }

        public Color get(float alpha) {
            return withAlpha(normal.get(), alpha);
        }

        public Color get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
            if (pressed) return this.pressed.get();
            return (hovered && (bypassDisableHoverColor || !disableHoverColor)) ? this.hovered.get() : this.normal.get();
        }

        public Color get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor, float alpha) {
            Color color = get(pressed, hovered, bypassDisableHoverColor);
            return withAlpha(color, alpha);
        }

        public Color get(boolean pressed, boolean hovered) {
            return get(pressed, hovered, false);
        }

        public Color get(boolean pressed, boolean hovered, float alpha) {
            return get(pressed, hovered, false, alpha);
        }

        public Color get(boolean hovered) {
            return get(false, hovered, false);
        }

        public Color get(boolean hovered, float alpha) {
            return get(false, hovered, false, alpha);
        }

        private Color withAlpha(Color color, float alpha) {
            Color result = color.copy().a((int) (255 * alpha));
            result.validate();
            return result;
        }
    }
}
