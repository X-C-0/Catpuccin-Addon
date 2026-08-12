package me.pindour.catppuccin.api.animation;

import java.util.function.Function;

public enum Easing {
    LINEAR(t -> t),
    
    QUAD_OUT(t -> 1.0 - (1.0 - t) * (1.0 - t)),
    QUAD_IN_OUT(t -> t < 0.5 ? 2.0 * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0),

    QUART_OUT(t -> 1.0 - Math.pow(1.0 - t, 4.0)),
    QUART_IN_OUT(t -> t < 0.5 ? 8.0 * t * t * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 4.0) / 2.0),
    
    BACK_OUT(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }),
    BACK_IN_OUT(t -> {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        return t < 0.5
                ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
                : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
    });

    private final Function<Double, Double> function;

    Easing(Function<Double, Double> function) {
        this.function = function;
    }

    public double apply(double t) {
        return function.apply(t);
    }
}
