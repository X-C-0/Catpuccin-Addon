plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2" /* [SC] DO NOT EDIT */

tasks.register("buildAllAndCollect") {
    group = "build"
    description = "Builds all Stonecutter versions and collects the mod jars into build/libs/<modVersion>/"
    stonecutter.versions.forEach { ver ->
        dependsOn(":${ver.version}:buildAndCollect")
    }
}

stonecutter parameters {
    replacements {
        string(current.parsed < "26.2") {
            // Screen
            replace("mc.gui.screen()", "mc.screen")
            replace("mc.gui.setScreen(", "mc.setScreen(")
            // Render
            replace("Minecraft.getInstance().gameRenderer.mainRenderTarget()", "Minecraft.getInstance().getMainRenderTarget()")
            // Options
            replace("mc.gameRenderer.gameRenderState().guiRenderState.isHudHidden", "mc.options.hideGui")
        }
        string(current.parsed <= "26.1") {
            // Screen
            replace("extractBackground(GuiGraphicsExtractor", "renderBackground(GuiGraphics")
            replace("super.extractBackground", "super.renderBackground")
            // DrawContext
            replace("net.minecraft.client.gui.GuiGraphicsExtractor", "net.minecraft.client.gui.GuiGraphics")
            replace("render(GuiGraphicsExtractor", "render(GuiGraphics")
            replace("context.verticalLine", "context.vLine")
            replace("context.horizontalLine", "context.hLine")
        }
        string(current.parsed <= "1.21.10") {
            // Identifier
            replace("Identifier", "ResourceLocation")
            // Mac OS
            replace("net.minecraft.util.Util", "net.minecraft.Util")
            // String utils
            replace("org.apache.commons.lang3.Strings", "org.apache.commons.lang3.StringUtils")
            replace("Strings.CI.contains", "StringUtils.containsIgnoreCase")
        }
        string(current.parsed <= "1.21.8") {
            // MouseButtonEvent -> mouseX, mouseY, button
            replace("onMouseClicked(MouseButtonEvent click", "onMouseClicked(double mouseX, double mouseY, int button")
            replace("onMouseReleased(MouseButtonEvent click", "onMouseReleased(double mouseX, double mouseY, int button")
            replace("mouseReleased(MouseButtonEvent click", "mouseReleased(double mouseX, double mouseY, int button")
            // CharInput -> char
            replace("onCharTyped(CharInput input)", "onCharTyped(char input)")
            // KeyEvent -> key, mods
            replace("onKeyRepeated(KeyEvent input)", "onKeyRepeated(int key, int mods)")
            replace("keyPressed(KeyEvent input)", "keyPressed(int keyCode, int scanCode, int modifiers)")
            replace("keyPressed(input)", "keyPressed(keyCode, scanCode, modifiers)")
        }
    }
}
