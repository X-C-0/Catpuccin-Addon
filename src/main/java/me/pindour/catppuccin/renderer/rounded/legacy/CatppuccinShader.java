package me.pindour.catppuccin.renderer.rounded.legacy;

//? if <=1.21.4 {
/*import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.pindour.catppuccin.CatppuccinAddon;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32C;

import meteordevelopment.meteorclient.renderer.GL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CatppuccinShader {
    private final int id;
    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    public CatppuccinShader(String vertPath, String fragPath) {
        int vert = GL.createShader(GL32C.GL_VERTEX_SHADER);
        GL.shaderSource(vert, read(vertPath));

        String vertError = GL.compileShader(vert);
        if (vertError != null) {
            throw new RuntimeException("Failed to compile vertex shader (" + vertPath + "): " + vertError);
        }

        int frag = GL.createShader(GL32C.GL_FRAGMENT_SHADER);
        GL.shaderSource(frag, read(fragPath));

        String fragError = GL.compileShader(frag);
        if (fragError != null) {
            throw new RuntimeException("Failed to compile fragment shader (" + fragPath + "): " + fragError);
        }

        id = GL.createProgram();

        String programError = GL.linkProgram(id, vert, frag);
        if (programError != null) {
            throw new RuntimeException("Failed to link program: " + programError);
        }

        GL.deleteShader(vert);
        GL.deleteShader(frag);
    }

    private String read(String path) {
        Identifier id = CatppuccinAddon.identifier("shaders/" + path);
        try (InputStream in = getResource(id)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read shader '" + id + "'", e);
        }
    }

    private InputStream getResource(Identifier id) throws IOException {
        Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).orElseThrow();
        return resource.getInputStream();
    }

    public void bind() {
        GL.useProgram(id);
    }

    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GL.getUniformLocation(id, name);
        uniformLocations.put(name, location);
        return location;
    }

    public void set(String name, boolean v) {
        GL.uniformInt(getLocation(name), v ? GL32C.GL_TRUE : GL32C.GL_FALSE);
    }

    public void set(String name, int v) {
        GL.uniformInt(getLocation(name), v);
    }

    public void set(String name, double v) {
        GL.uniformFloat(getLocation(name), (float) v);
    }

    public void set(String name, double v1, double v2) {
        GL.uniformFloat2(getLocation(name), (float) v1, (float) v2);
    }

    public void set(String name, Color color) {
        float r = (float) color.r / 255f;
        float g = (float) color.g / 255f;
        float b = (float) color.b / 255f;
        float a = (float) color.a / 255f;
        GL.uniformFloat4(getLocation(name), r, g, b, a);
    }

    public void set(String name, Matrix4f mat) {
        GL.uniformMatrix(getLocation(name), mat);
    }
}
*///?}