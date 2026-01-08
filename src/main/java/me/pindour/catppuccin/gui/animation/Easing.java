package me.pindour.catppuccin.gui.animation;

import java.util.function.Function;

public enum Easing {
    LINEAR(t -> t),
    QUAD_OUT(t -> 1.0 - (1.0 - t) * (1.0 - t)),
    CUBIC_OUT(t -> 1.0 - Math.pow(1.0 - t, 3.0)),
    QUART_OUT(t -> 1.0 - Math.pow(1.0 - t, 4.0));

    private final Function<Double, Double> function;

    Easing(Function<Double, Double> function) {
        this.function = function;
    }

    public double apply(double t) {
        return function.apply(t);
    }
}
