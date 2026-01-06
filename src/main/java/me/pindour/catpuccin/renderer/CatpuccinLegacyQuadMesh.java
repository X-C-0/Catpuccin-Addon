#if MC_VER <= MC_1_21_4
package me.pindour.catpuccin.renderer;

import meteordevelopment.meteorclient.renderer.GL;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32C;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class CatpuccinLegacyQuadMesh {
    private static final int FLOATS_PER_VERTEX = 8;
    private static final int VERTEX_COUNT = 4;
    private static final int STRIDE_BYTES = FLOATS_PER_VERTEX * Float.BYTES;

    private final int vao;
    private final int vbo;
    private final int ibo;
    private final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(VERTEX_COUNT * FLOATS_PER_VERTEX);

    public CatpuccinLegacyQuadMesh() {
        vao = GL.genVertexArray();
        GL.bindVertexArray(vao);

        vbo = GL.genBuffer();
        GL.bindVertexBuffer(vbo);
        GL32C.glBufferData(GL32C.GL_ARRAY_BUFFER, (long) VERTEX_COUNT * FLOATS_PER_VERTEX * Float.BYTES, GL32C.GL_DYNAMIC_DRAW);

        ibo = GL.genBuffer();
        GL.bindIndexBuffer(ibo);
        IntBuffer indices = BufferUtils.createIntBuffer(6);
        indices.put(0).put(1).put(2).put(2).put(3).put(0).flip();
        GL32C.glBufferData(GL32C.GL_ELEMENT_ARRAY_BUFFER, indices, GL32C.GL_STATIC_DRAW);

        GL.enableVertexAttribute(0);
        GL.vertexAttribute(0, 2, GL32C.GL_FLOAT, false, STRIDE_BYTES, 0L);
        GL.enableVertexAttribute(1);
        GL.vertexAttribute(1, 2, GL32C.GL_FLOAT, false, STRIDE_BYTES, 2L * Float.BYTES);
        GL.enableVertexAttribute(2);
        GL.vertexAttribute(2, 4, GL32C.GL_FLOAT, false, STRIDE_BYTES, 4L * Float.BYTES);

        GL.bindVertexArray(0);
        GL.bindVertexBuffer(0);
        GL.bindIndexBuffer(0);
    }

    public void render(float x, float y, float width, float height) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        vertexBuffer.clear();
        putVertex(x, y, -halfWidth, -halfHeight);
        putVertex(x, y + height, -halfWidth, halfHeight);
        putVertex(x + width, y + height, halfWidth, halfHeight);
        putVertex(x + width, y, halfWidth, -halfHeight);
        vertexBuffer.flip();

        GL.bindVertexArray(vao);
        GL.bindVertexBuffer(vbo);
        GL32C.glBufferSubData(GL32C.GL_ARRAY_BUFFER, 0, vertexBuffer);
        GL.bindIndexBuffer(ibo);
        GL.drawElements(GL32C.GL_TRIANGLES, 6, GL32C.GL_UNSIGNED_INT);
        GL.bindVertexArray(0);
        GL.bindIndexBuffer(0);
    }

    private void putVertex(float x, float y, float localX, float localY) {
        vertexBuffer.put(x).put(y).put(localX).put(localY).put(1f).put(1f).put(1f).put(1f);
    }
}
#endif
