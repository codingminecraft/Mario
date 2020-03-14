package com.renderer;

import com.dataStructure.AssetPool;
import com.jade.GameObject;
import com.jade.Window;
import com.util.JMath;
import com.util.enums.DataType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class UIRenderBatch implements Comparable {
    /*
    /*      Vertex
    /*     ======
    /*     Pos                      Color                       TexCoord         BorderRadius              BorderColor                BorderWidth     Dimensions
    /*     123.0f, 232.0f, 10.f,    0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 0.0f,      0.1f, 0.1f, 0.3f, 0.3f    0.0f, 0.0f, 0.0f, 0.0f     1.0f            64.0f, 64.0f
     */

    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORD_SIZE = 2;
    private final int BORDER_RADIUS_SIZE = 4;
    private final int BORDER_COLOR_SIZE = 4;
    private final int BORDER_WIDTH_SIZE = 1;
    private final int DIMENSIONS_SIZE = 2;

    private final int POS_OFFSET = 0 * JMath.sizeof(DataType.FLOAT);
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int BORDER_RADIUS_OFFSET = TEX_COORD_OFFSET + TEX_COORD_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int BORDER_COLOR_OFFSET = BORDER_RADIUS_OFFSET + BORDER_RADIUS_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int BORDER_WIDTH_OFFSET = BORDER_COLOR_OFFSET + BORDER_COLOR_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int DIMENSIONS_OFFSET = BORDER_WIDTH_OFFSET + BORDER_WIDTH_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int VERTEX_SIZE = 20;
    private final int VERTEX_SIZE_BYTES = JMath.sizeof(DataType.FLOAT) * VERTEX_SIZE;

    private List<UIRenderComponent> renderables;
    private float[] vertices;
    private int[] indices;
    private FloatBuffer verticesBuffer;
    private Shader shader;
    private Renderer renderer;
    private int maxBatchSize;

    private int vaoID, vboID, eboID;

    public int zIndex;
    public boolean hasRoom = true;

    public UIRenderBatch(int maxBatchSize, Renderer renderer, int zIndex) {
        this.shader = AssetPool.getShader("assets/shaders/uiShader.glsl");
        this.renderables = new ArrayList<>();
        this.maxBatchSize = maxBatchSize;

        // 4 Vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);

        // 6 indices per quad (3 per triangle)
        indices = new int[maxBatchSize * 6];
        this.renderer = renderer;

        this.zIndex = zIndex;
    }

    public void start() {
        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a FloatBuffer of vertices
        verticesBuffer.put(vertices).flip();

        // Create an IntBuffer of the indices
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        // Create a Buffer Object and upload the vertices buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_DYNAMIC_DRAW);

        // Create another buffer object for the indices, then upload the indices
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_DYNAMIC_DRAW);

        // Enable the buffer to know which coords are positions and which ones are
        // colors...
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, BORDER_RADIUS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, BORDER_RADIUS_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, BORDER_COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, BORDER_COLOR_OFFSET);
        glEnableVertexAttribArray(4);

        glVertexAttribPointer(5, BORDER_WIDTH_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, BORDER_WIDTH_OFFSET);
        glEnableVertexAttribArray(5);

        glVertexAttribPointer(6, DIMENSIONS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, DIMENSIONS_OFFSET);
        glEnableVertexAttribArray(6);
    }

    public void add(UIRenderComponent renderable) {
        // Get index and add renderable
        int index = renderables.size();
        renderables.add(renderable);
        // Add properties to local array
        loadVertexProperties(index);
        loadElementIndices(index);

        if (renderables.size() >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < renderables.size(); i++) {
            UIRenderComponent renderable = renderables.get(i);
            if (renderable.isDirty) {
                loadVertexProperties(i);
                renderable.isDirty = false;
                rebufferData = true;
            }
        }
        if (rebufferData) {
            verticesBuffer.put(vertices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, verticesBuffer);
        }

        // Use our program
        shader.use();
        shader.uploadMat4f("uProjection", renderer.camera().getProjectionMatrix());
        shader.uploadMat4f("uView", renderer.camera().getFixedViewMatrix());
        shader.uploadFloat("uAspect", Window.getWindow().getAsepct());

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glEnableVertexAttribArray(5);
        glEnableVertexAttribArray(6);

        // Draw the batch
        glDrawElements(GL_TRIANGLES, renderables.size() * 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glDisableVertexAttribArray(5);
        glDisableVertexAttribArray(6);
        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();
    }

    public void loadVertexProperties(int index) {
        UIRenderComponent renderable = renderables.get(index);
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;

        Vector4f color = renderable.getColor();

        Vector2f[] texCoords = renderable.getTexCoords();
        assert(texCoords.length == 4);

        Vector4f borderRadius = renderable.getBorderRadius();
        Vector4f borderColor = renderable.getBorderColor();
        float borderWidth = renderable.getBorderWidth();

        // Add 4 vertices with the appropriate properties to vertex array
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = renderable.getPosX() + (xAdd * renderable.getWidth());
            vertices[offset + 1] = renderable.getPosY() + (yAdd * renderable.getHeight());
            vertices[offset + 2] = renderable.getZIndex();

            // Load color
            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            // Load tex coords
            vertices[offset + 7] = texCoords[i].x;
            vertices[offset + 8] = texCoords[i].y;

            // Load border radius
            vertices[offset + 9] = borderRadius.x;
            vertices[offset + 10] = borderRadius.y;
            vertices[offset + 11] = borderRadius.z;
            vertices[offset + 12] = borderRadius.w;

            // Load border color
            vertices[offset + 13] = borderColor.x;
            vertices[offset + 14] = borderColor.y;
            vertices[offset + 15] = borderColor.z;
            vertices[offset + 16] = borderColor.w;

            // Load border width
            vertices[offset + 17] = borderWidth;

            // Load dimensions
            vertices[offset + 18] = renderable.getWidth();
            vertices[offset + 19] = renderable.getHeight();

            offset += VERTEX_SIZE;
        }
    }

    private void loadElementIndices(int index) {
        int offsetArray = 6 * index;
        int offset = 4 * index;

        // Triangle 1
        indices[offsetArray] = offset + 3;
        indices[offsetArray + 1] = offset + 2;
        indices[offsetArray + 2] = offset + 0;

        // Triangle 2
        indices[offsetArray + 3] = offset + 0;
        indices[offsetArray + 4] = offset + 2;
        indices[offsetArray + 5] = offset + 1;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof RenderBatch)) return -1;
        RenderBatch batch = (RenderBatch)o;
        return Integer.compare(batch.zIndex, this.zIndex);
    }
}
