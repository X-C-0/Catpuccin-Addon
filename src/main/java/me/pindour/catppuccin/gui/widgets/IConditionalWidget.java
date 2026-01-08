package me.pindour.catppuccin.gui.widgets;

import java.util.function.BooleanSupplier;

public interface IConditionalWidget {
    BooleanSupplier getVisibilityCondition();

    void setVisibilityCondition(BooleanSupplier condition);

    default boolean shouldRender(boolean mouseOver) {
        BooleanSupplier condition = getVisibilityCondition();
        return condition == null || condition.getAsBoolean() || mouseOver;
    }
}