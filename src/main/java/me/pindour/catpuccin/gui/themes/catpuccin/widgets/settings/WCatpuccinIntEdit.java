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
import meteordevelopment.meteorclient.settings.IntSetting;

public class WCatpuccinIntEdit extends WVerticalList implements CatpuccinWidget {
    private final IntSetting setting;
    private int value;

    public final int min, max;
    private final int sliderMin, sliderMax;
    public boolean noSlider = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WCatpuccinTextBox textBox;
    private WSlider slider;

    public WCatpuccinIntEdit(IntSetting setting) {
        this.setting = setting;
        this.value = setting.get();
        this.min = setting.min;
        this.max = setting.max;
        this.sliderMin = setting.sliderMin;
        this.sliderMax = setting.sliderMax;

        if (setting.noSlider || (sliderMin == 0 && sliderMax == 0)) this.noSlider = true;
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
        list.add(theme().label(setting.title + ":")).widget().tooltip = setting.description;

        // Value
        textBox = (WCatpuccinTextBox) list.add(theme().textBox(Integer.toString(value), this::filter, 0)).expandX().expandCellX().widget();
        textBox.shouldRenderBackground(false);
        textBox.color(theme().accentColor());

        // Reset
        WidgetUtils.reset(list, setting, () -> set(setting.get()), this::showReset);

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
