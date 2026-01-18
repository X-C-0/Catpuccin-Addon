package me.pindour.catppuccin.api.animation;

public enum Direction {
    FORWARDS, BACKWARDS;

    public Direction opposite() {
        return this == FORWARDS ? BACKWARDS : FORWARDS;
    }

    public boolean isForwards() {
        return this == FORWARDS;
    }
}
