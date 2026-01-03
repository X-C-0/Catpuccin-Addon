package me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinTextBox;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WCatpuccinDoubleEdit extends WHorizontalList implements CatpuccinWidget {
    private double value;

    private final double min, max;
    private final double sliderMin, sliderMax;

    public int decimalPlaces;
    public boolean noSlider = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WCatpuccinTextBox textBox;
    private WSlider slider;

    public WCatpuccinDoubleEdit(double value, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
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
        // Slider or buttons
        if (noSlider) {
            add(theme.button("+")).minWidth(30).widget().action = () -> setButton(get() + 1);
            add(theme.button("-")).minWidth(30).widget().action = () -> setButton(get() - 1);
        }
        else {
            slider = add(theme.slider(value, sliderMin, sliderMax)).padHorizontal(6).minWidth(250).expandX().widget();
        }

// Value
        double reservedWith = Math.max(
                theme.textWidth(String.valueOf(sliderMin)),
                theme.textWidth(String.valueOf(sliderMax))
        );

        textBox = (WCatpuccinTextBox) add(theme
                .textBox(valueString(), this::filter))
                .minWidth(reservedWith + pad())
                .right()
                .widget();

        textBox.shouldRenderBackground(false);
//        textBox.setDynamicWidth(true);
        textBox.color(theme().accentColor());

        textBox.actionOnUnfocused = () -> {
            double lastValue = value;

            switch (textBox.get()) {
                case "", ".", "-." -> value = 0;
                case "-" -> value = -0;
                default -> {
                    try {
                        value = Double.parseDouble(textBox.get());
                    } catch (NumberFormatException ignored) { }
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
        if (bd.scale() < 1) bd = bd.setScale(1, RoundingMode.UNNECESSARY);

        return bd.toPlainString();
    }
}
