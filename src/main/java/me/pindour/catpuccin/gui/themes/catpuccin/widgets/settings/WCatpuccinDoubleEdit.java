package me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinTextBox;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;

import java.util.Locale;

public class WCatpuccinDoubleEdit extends WVerticalList implements CatpuccinWidget {
    private final String title;
    private final String description;
    private double value;

    private final double min, max;
    private final double sliderMin, sliderMax;

    public int decimalPlaces;
    public boolean noSlider = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WCatpuccinTextBox textBox;
    private WSlider slider;

    public WCatpuccinDoubleEdit(String title, String description, double value, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
        this.title = title;
        this.description = description;
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
        WHorizontalList list = add(theme.horizontalList()).expandX().padHorizontal(8).padTop(6).widget();

        // Title
        list.add(theme.label(title, true)).centerY().expandCellX().widget().tooltip = description;

        // Value
        textBox = (WCatpuccinTextBox) list.add(theme.textBox(valueString(), this::filter)).right().widget();
        textBox.setRenderBackground(false);
        textBox.setDynamicWidth(true);
        textBox.color(theme().accentColor());

        // Slider or buttons
        if (noSlider) {
            list.add(theme.button("+")).minWidth(30).widget().action = () -> setButton(get() + 1);
            list.add(theme.button("-")).minWidth(30).widget().action = () -> setButton(get() - 1);
        }
        else {
            WHorizontalList sliderList = add(theme.horizontalList()).expandX().padHorizontal(8).padBottom(6).widget();

            sliderList.add(theme.label(String.valueOf(sliderMin)));
            slider = sliderList.add(theme.slider(value, sliderMin, sliderMax)).padHorizontal(6).minWidth(200).expandX().widget();
            sliderList.add(theme.label(String.valueOf(sliderMax)));
        }

        textBox.actionOnUnfocused = () -> {
            double lastValue = value;

            if (textBox.get().isEmpty()) value = 0;
            else if (textBox.get().equals("-")) value = -0;
            else if (textBox.get().equals(".")) value = 0;
            else if (textBox.get().equals("-.")) value = 0;
            else {
                try {
                    value = Double.parseDouble(textBox.get());
                } catch (NumberFormatException ignored) {}
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

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderBackground(this, false, false);
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
        return String.format(Locale.US, "%." + decimalPlaces + "f", value);
    }
}
