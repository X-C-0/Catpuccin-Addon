package me.pindour.catpuccin.gui.themes.catpuccin.widgets.settings;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.input.WCatpuccinTextBox;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;

public class WCatpuccinIntEdit extends WVerticalList implements CatpuccinWidget {
    private final String title;
    private final String description;
    public WHorizontalList header;

    private int value;

    public final int min, max;
    private final int sliderMin, sliderMax;
    public boolean noSlider = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WCatpuccinTextBox textBox;
    private WSlider slider;

    public WCatpuccinIntEdit(String title, String description, int value, int min, int max, int sliderMin, int sliderMax, boolean noSlider) {
        this.title = title;
        this.description = description;
        this.value = value;
        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;

        if (noSlider || (sliderMin == 0 && sliderMax == 0)) this.noSlider = true;
    }

    @Override
    public void init() {
        header = add(theme.horizontalList()).expandX().widget();

        // Buttons
        if (noSlider) {
            header.add(theme.button("+")).minWidth(30).widget().action = () -> setButton(get() + 1);
            header.add(theme.button("-")).minWidth(30).widget().action = () -> setButton(get() - 1);
        }

        // Title
        header.add(theme().label(title + ":")).bottom().widget().tooltip = description;

        // Value
        textBox = (WCatpuccinTextBox) header.add(theme().textBox(Integer.toString(value), this::filter, 0)).expandX().bottom().widget();
        textBox.shouldRenderBackground(false);
        textBox.color(theme().accentColor());

        // Slider
        if (!noSlider) {
            slider = add(theme.slider(value, sliderMin, sliderMax)).minWidth(200).expandX().widget();
        }

        textBox.actionOnUnfocused = () -> {
            int lastValue = value;

            if (textBox.get().isEmpty()) value = 0;
            else if (textBox.get().equals("-")) value = -0;
            else {
                try {
                    value = Integer.parseInt(textBox.get());
                } catch (NumberFormatException ignored) {}
            }

            if (slider != null) slider.set(value);

            if (value != lastValue) {
                if (action != null) action.run();
                if (actionOnRelease != null) actionOnRelease.run();
            }
        };

        if (slider != null) {
            slider.action = () -> {
                int lastValue = value;

                value = (int) Math.round(slider.get());
                textBox.set(Integer.toString(value));

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
        else good = Character.isDigit(c);

        if (good && validate) {
            try {
                Integer.parseInt(text + c);
            } catch (NumberFormatException ignored) {
                good = false;
            }
        }

        return good;
    }

    private void setButton(int v) {
        if (this.value == v) return;

        if (v < min) this.value = min;
        else this.value = Math.min(v, max);

        if (this.value == v) {
            textBox.set(Integer.toString(this.value));
            if (slider != null) slider.set(this.value);

            if (action != null) action.run();
            if (actionOnRelease != null) actionOnRelease.run();
        }
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;

        textBox.set(Integer.toString(value));
        if (slider != null) slider.set(value);
    }

    public boolean showReset() {
        return mouseOver || (slider != null && slider.focused);
    }
}
