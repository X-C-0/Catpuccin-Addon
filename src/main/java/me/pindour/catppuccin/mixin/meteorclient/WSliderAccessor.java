package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.10
//import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WSlider.class, remap = false)
public interface WSliderAccessor {
    //? if <=1.21.10 {
    /*@Accessor("handleMouseOver")
    boolean catppuccin$isHandleMouseOver();
    *///? }
}

