package me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings;

import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.TextSize;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinTextBox;
import me.pindour.catpuccin.utils.ColorUtils;
import me.pindour.catpuccin.utils.WidgetUtils;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.settings.DoubleSetting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WCatpuccinDoubleEdit extends WVerticalList implements CatpuccinWidget {
    private final String title;
    private final String description;
    private final DoubleSetting setting;
    private double value;

    private final double min, max;
    private final double sliderMin, sliderMax;

    public int decimalPlaces;
    public boolean noSlider = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WCatpuccinTextBox textBox;
    private WSlider slider;

    public WCatpuccinDoubleEdit(DoubleSetting setting) {
        this(setting.title,
            setting.description,
            setting,
            setting.get(),
            setting.min,
            setting.max,
            setting.decimalPlaces,
            setting.sliderMin,
            setting.sliderMax,
            setting.noSlider
        );
    }

    public WCatpuccinDoubleEdit(String title, String description, DoubleSetting setting, double value, double min, double max, int decimalPlaces, double sliderMin, double sliderMax, boolean noSlider) {
        this.title = title;
        this.description = description;
        this.setting = setting;
        this.value = value;
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;

        if (noSlider || (sliderMin == 0 && sliderMax == 0)) this.noSlider = true;
    }

    @Override
    public void init() {
        WHorizontalList list = add(theme.horizontalList()).expandX().widget();

        // Buttons
        if (noSlider) {
            list.add(theme.button("+")).minWidth(30).widget().action = () -> setButton(get() + 1);
            list.add(theme.button("-")).minWidth(30).widget().action = () -> setButton(get() - 1);
        }

        // Title
        list.add(theme().label(title + ":")).widget().tooltip = description;

        // Value
        textBox = (WCatpuccinTextBox) list.add(theme().textBox(valueString(), this::filter, 0)).expandX().expandCellX().widget();
        textBox.shouldRenderBackground(false);
        textBox.color(theme().accentColor());

        // Reset
        if (setting != null) WidgetUtils.reset(list, setting, () -> set(setting.get()), this::showReset);

        if (!noSlider) {
            WHorizontalList sliderList = add(theme.horizontalList()).expandX().padHorizontal(8).padBottom(6).widget();

            // Min label
            RichText minText = RichText
                    .of(String.valueOf(sliderMin))
                    .scale(TextSize.SMALL.get());

            sliderList.add(theme().label(minText)
                    .color(ColorUtils.withAlpha(theme().textSecondaryColor(), 0.5)))
                    .padLeft(pad() / 2);

            // Slider
            slider = sliderList.add(theme.slider(value, sliderMin, sliderMax))
                    .padHorizontal(6)
                    .minWidth(200)
                    .expandX()
                    .widget();

            // Max label
            RichText maxText = RichText
                    .of(String.valueOf(sliderMax))
                    .scale(TextSize.SMALL.get());

            sliderList.add(theme().label(maxText)
                    .color(ColorUtils.withAlpha(theme().textSecondaryColor(), 0.5)))
                    .padRight(pad() / 2);
        }

        textBox.actionOnUnfocused = () -> {
            double lastValue = value;

            switch (textBox.get()) {
                case "", ".", "-." -> value = 0;
                case "-" -> value = -0;
                default -> {
                    try {
                        value = Double.parseDouble(textBox.get());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            double preValidationValue = value;

            if (value < min) value = min;
            else if (value > max) value = max;

            if (value != preValidationValue) textBox.set(valueString());
            if (slider != null) slider.set(value);

            if (value != lastValue) {
                if (action != null) action.run();
                if (actionOnRelease != null) actionOnRelease.run();
            }
        };

        if (slider != null) {
            slider.action = () -> {
                double lastValue = value;

                value = slider.get();
                textBox.set(valueString());

                if (action != null && value != lastValue) action.run();
            };

            slider.actionOnRelease = () -> {
                if (actionOnRelease != null) actionOnRelease.run();
            };
        }
    }

    private boolean filter(String text, char c) {
        boolean good;
        boolean validate = true;

        if (c == '-' && !text.contains("-") && textBox.getCursor() == 0) {
            good = true;
            validate = false;
        }
        else if (c == '.' && !text.contains(".")) {
            good = true;
            if (text.isEmpty()) validate = false;
        }
        else good = Character.isDigit(c);

        if (good && validate) {
            try {
                Double.parseDouble(text + c);
            } catch (NumberFormatException ignored) {
                good = false;
            }
        }

        return good;
    }

    private void setButton(double v) {
        if (this.value == v) return;

        if (v < min) this.value = min;
        else this.value = Math.min(v, max);

        if (this.value == v) {
            textBox.set(valueString());
            if (slider != null) slider.set(this.value);

            if (action != null) action.run();
            if (actionOnRelease != null) actionOnRelease.run();
        }
    }

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;

        textBox.set(valueString());
        if (slider != null) slider.set(value);
    }

    private String valueString() {
        BigDecimal bd = BigDecimal.valueOf(value)
            .setScale(decimalPlaces, RoundingMode.HALF_UP)
            .stripTrailingZeros();

        // Ensures the number has at least one decimal digit (e.g. 3 → 3.0) so it doesn't look stupid
        if (bd.scale() < 1) bd = bd.setScale(1, RoundingMode.HALF_UP);

        return bd.toPlainString();
    }

    public boolean showReset() {
        return mouseOver || (slider != null && slider.focused);
    }
}
