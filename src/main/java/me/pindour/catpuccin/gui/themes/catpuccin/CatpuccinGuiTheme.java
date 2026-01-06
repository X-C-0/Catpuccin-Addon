package me.pindour.catpuccin.gui.themes.catpuccin;

import me.pindour.catpuccin.gui.animation.AnimationType;
import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.screens.CatpuccinModuleScreen;
import me.pindour.catpuccin.gui.screens.CatpuccinModulesScreen;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.RichTextRenderer;
import me.pindour.catpuccin.gui.text.RichTextSegment;
import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinAccentColor;
import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinColor;
import me.pindour.catpuccin.gui.themes.catpuccin.flavor.CatppuccinFlavors;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.*;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.container.WCatpuccinSection;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.container.WCatpuccinView;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.container.WCatpuccinWindow;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinDropdown;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinMultiSelect;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinSlider;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinTextBox;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable.*;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinDoubleEdit;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinIntEdit;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings.WCatpuccinKeybind;
import me.pindour.catpuccin.gui.widgets.WIcon;
import me.pindour.catpuccin.gui.widgets.input.WMultiSelect;
import me.pindour.catpuccin.gui.widgets.pressable.WColorPicker;
import me.pindour.catpuccin.gui.widgets.pressable.WOpenIndicator;
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

#if MC_VER < MC_1_21_10
import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;
#endif
#if MC_VER >= MC_1_21_10
import net.minecraft.client.util.MacWindowUtil;
#endif

public class CatpuccinGuiTheme extends GuiTheme {
    private final Map<CatppuccinColor, Color> colorCache;

    private RichTextRenderer textRenderer;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
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
            .description("Adds item icons to module categories.")
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
            .description("Renders a small outline around some UI elements for better visibility.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> roundedCorners = sgGeneral.add(new BoolSetting.Builder()
            .name("rounded-corners")
            .description("Makes the corners of most UI elements rounded.")
            .defaultValue(true)
            .build()
    );

    // Animations

    public final Setting<AnimationType> guiAnimation = sgAnimations.add(new EnumSetting.Builder<AnimationType>()
            .name("animation-type")
            .description("Animation to play when interacting with the UI.")
            .defaultValue(AnimationType.EaseOut)
            .build()
    );

    public final Setting<Integer> guiAnimationSpeed = sgAnimations.add(new IntSetting.Builder()
            .name("animation-speed")
            .description("Speed of the animation in milliseconds.")
            .defaultValue(300)
            .sliderRange(1, 1000)
            .build()
    );

    // Colors

    public final Setting<CatppuccinFlavors> flavor = sgColors.add(new EnumSetting.Builder<CatppuccinFlavors>()
            .name("Flavor")
            .description("Main color (flavor) of the UI.")
            .defaultValue(CatppuccinFlavors.Macchiato)
            .onChanged(this::updateCache)
            .build()
    );

    private final Setting<CatppuccinAccentColor> accentColor = sgColors.add(new EnumSetting.Builder<CatppuccinAccentColor>()
            .name("Accent")
            .description("Main color of the UI.")
            .defaultValue(CatppuccinAccentColor.Mauve)
            .build()
    );

    public final Setting<Integer> windowOpacity = sgColors.add(new IntSetting.Builder()
            .name("window-opacity")
            .description("How much opaque the windows should be.")
            .defaultValue(255)
            .sliderRange(0, 255)
            .build()
    );

    public final Setting<Integer> backgroundOpacity = sgColors.add(new IntSetting.Builder()
            .name("background-opacity")
            .description("How much opaque the backgrounds should be.")
            .defaultValue(255)
            .sliderRange(0, 255)
            .build()
    );

    // Snapping

    public final Setting<Boolean> snapModuleCategories = sgSnapping.add(new BoolSetting.Builder()
            .name("snap-module-categories")
            .description("Makes the category windows snap to the grid.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Integer> snappingGridSize = sgSnapping.add(new IntSetting.Builder()
            .name("grid-size")
            .description("How big should the snapping grid be.")
            .defaultValue(10)
            .sliderRange(5, 50)
            .build()
    );

    // Screens

    public final Setting<Boolean> catpuccinEntityTypeListScreen = sgScreens.add(new BoolSetting.Builder()
            .name("entity-type-list-screen")
            .description("Replace Meteor's screen with a Catpuccin screen. This screen is used to select entities for a setting.")
            .defaultValue(true)
            .build()
    );

    // Three state colors

    public final ThreeStateColor backgroundColor = new ThreeStateColor(
            this::mantleColor,
            this::surface0Color,
            this::surface0Color
    );

    public final ThreeStateColor outlineColor = new ThreeStateColor(
            this::surface0Color,
            this::surface2Color,
            this::surface2Color
    );

    public final ThreeStateColor scrollbarColor = new ThreeStateColor(
            this::surface0Color,
            this::surface1Color,
            this::overlay2Color
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

    public CatpuccinGuiTheme() {
        super("Catpuccin");

        settingsFactory = new CatpuccinSettingsWidgetFactory(this);
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
        return w(new WCatpuccinWindow(icon, title));
    }

    public WLabel label(RichText text, double maxWidth) {
        if (maxWidth == 0) return w(new WCatpuccinLabel(text));
        return w(new WCatpuccinMultiLabel(text, maxWidth));
    }

    public WLabel label(RichText text) {
        return label(text, 0);
    }

    @Override
    public WLabel label(String text, boolean title, double maxWidth) {
        if (maxWidth == 0) return w(new WCatpuccinLabel(RichText.of(text).boldIf(title)));
        return w(new WCatpuccinMultiLabel(RichText.of(text).boldIf(title), maxWidth));
    }

    @Override
    public WHorizontalSeparator horizontalSeparator(String text) {
        return w(new WCatpuccinHorizontalSeparator(text));
    }

    @Override
    public WVerticalSeparator verticalSeparator() {
        return w(new WCatpuccinVerticalSeparator());
    }

    public WCatpuccinButton button(RichText text, GuiTexture texture) {
        return w(new WCatpuccinButton(text, texture));
    }

    public WCatpuccinButton button(RichText text) {
        return button(text, null);
    }

    @Override
    public WButton button(String text, GuiTexture texture) {
        return button(RichText.of(text), texture);
    }

    @Override
    public WButton button(GuiTexture texture) {
        return w(new WCatpuccinButton(texture));
    }

    #if MC_VER >= MC_1_21_8
    @Override
    protected WConfirmedButton confirmedButton(String text, String confirmText, GuiTexture texture) {
        return w(new WCatpuccinConfirmedButton(text, confirmText, texture));
    }
    #endif

    @Override
    public WMinus minus() {
        return w(new WCatpuccinMinus());
    }

    #if MC_VER >= MC_1_21_8
    @Override
    public WConfirmedMinus confirmedMinus() {
        return w(new WCatpuccinConfirmedMinus());
    }
    #endif

    @Override
    public WPlus plus() {
        return w(new WCatpuccinPlus());
    }

    @Override
    public WCheckbox checkbox(boolean checked) {
        return w(new WCatpuccinCheckbox(checked));
    }

    @Override
    public WSlider slider(double value, double min, double max) {
        return w(new WCatpuccinSlider(value, min, max));
    }

    @Override
    public WTextBox textBox(String text, String placeholder, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
        return w(new WCatpuccinTextBox(text, placeholder, filter, renderer));
    }

    public <T> WDropdown<T> dropdown(String title, T[] values, T value) {
        return w(new WCatpuccinDropdown<>(title, values, value));
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
        return w(new WCatpuccinTriangle());
    }

    @Override
    public WTooltip tooltip(String text) {
        return w(new WCatpuccinTooltip(text));
    }

    @Override
    public WView view() {
        return w(new WCatpuccinView());
    }

    @Override
    public WSection section(String title, boolean expanded, WWidget headerWidget) {
        return w(new WCatpuccinSection(title, expanded, headerWidget));
    }

    @Override
    public WAccount account(WidgetScreen screen, Account<?> account) {
        return w(new WCatpuccinAccount(screen, account));
    }

    @Override
    public WWidget module(Module module) {
        return w(createModuleWidget(module, module.title));
    }

    #if MC_VER >= MC_1_21_11
    @Override
    public WWidget module(Module module, String title) {
        return w(createModuleWidget(module, title));
    }
    #endif

    @Override
    public WQuad quad(Color color) {
        return w(new WCatpuccinQuad(color));
    }

    @Override
    public WTopBar topBar() {
        return w(new WCatpuccinTopBar());
    }

    @Override
    public WFavorite favorite(boolean checked) {
        return w(new WCatpuccinFavorite(checked));
    }

    public WCatpuccinKeybind catpuccinKeybind(Keybind keybind) {
        return catpuccinKeybind(keybind, Keybind.none());
    }

    public WCatpuccinKeybind catpuccinKeybind(Keybind keybind, Keybind defaultValue) {
        return catpuccinKeybind(null, keybind, defaultValue);
    }

    public WCatpuccinKeybind catpuccinKeybind(String title, Keybind keybind, Keybind defaultValue) {
        return w(new WCatpuccinKeybind(title, keybind, defaultValue));
    }

    public WOpenIndicator openIndicator(boolean open) {
        return w(new WCatpuccinOpenIndicator(open));
    }

    public WIcon icon(double size, CatpuccinIcons icon) {
        return w(new WCatpuccinIcon(size, icon.texture()));
    }

    public WColorPicker colorPicker(Color color, GuiTexture overlayTexture) {
        return w(new WCatpuccinColorPicker(color, overlayTexture));
    }

    public <T> WMultiSelect<T> multiSelect(String title, List<T> items) {
        return w(new WCatpuccinMultiSelect<>(title, items));
    }

    // Settings widgets

    public WCatpuccinIntEdit catpuccinIntEdit(String title, String description, int value, int min, int max, int sliderMin, int sliderMax, boolean noSlider) {
        return w(new WCatpuccinIntEdit(title, description, value, min, max, sliderMin, sliderMax, noSlider));
    }

    public WCatpuccinDoubleEdit catpuccinDoubleEdit(String title, String description, double value, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
        return w(new WCatpuccinDoubleEdit(title, description, value, min, max, sliderMin, sliderMax, decimalPlaces, noSlider));
    }

    // Animations

    public AnimationType uiAnimationType() {
        return guiAnimation.get();
    }

    public int uiAnimationSpeed() {
        return guiAnimationSpeed.get();
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

    public Color titleTextColor() {
        return colorCache.get(CatppuccinColor.Text);
    }

    public Color textColor() {
        return colorCache.get(CatppuccinColor.Subtext1);
    }

    public Color textSecondaryColor() {
        return colorCache.get(CatppuccinColor.Subtext0);
    }

    public Color textHighlightColor() {
        return colorCache.get(CatppuccinColor.Blue);
    }

    // Opacity

    public int windowOpacity() {
        return windowOpacity.get();
    }

    public int backgroundOpacity() {
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
        return new CatpuccinModulesScreen(this);
    }

    @Override
    public boolean isModulesScreen(Screen screen) {
        return screen instanceof CatpuccinModulesScreen;
    }

    @Override
    public WidgetScreen moduleScreen(Module module) {
        return new CatpuccinModuleScreen(this, module);
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
        CatpuccinRenderer.get().setTheme(this);
    }

    @Override
    public double scale(double value) {
        double scaled = value * scale.get();

        if (isMac()) {
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

    private boolean isMac() {
        #if MC_VER >= MC_1_21_10
        return MacWindowUtil.IS_MAC;
        #else
        return IS_SYSTEM_MAC;
        #endif
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

        public Color get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
            if (pressed) return this.pressed.get();
            return (hovered && (bypassDisableHoverColor || !disableHoverColor)) ? this.hovered.get() : this.normal.get();
        }

        public Color get(boolean pressed, boolean hovered) {
            return get(pressed, hovered, false);
        }

        public Color get(boolean hovered) {
            return get(false, hovered, false);
        }
    }

    private WWidget createModuleWidget(Module module, String title) {
        return new WCatpuccinModule(module, title);
    }
}
