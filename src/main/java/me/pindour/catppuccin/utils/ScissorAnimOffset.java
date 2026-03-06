package me.pindour.catppuccin.utils;

public class ScissorAnimOffset {
    /** True during the translated render pass — scissors should be offset */
    public static boolean active = false;
    /** The Y offset in scaled GUI coordinates */
    public static double offsetY = 0.0;
}
