package com.renderer;

import com.dataStructure.AssetPool;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec4f;
import com.util.JMath;
import com.util.enums.DataType;
import org.joml.Vector3f;
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

public class RenderBatch {
    /*
    /*      Vertex
    /*     ======
    /*     Pos                      Scale                Color                       TexCoord
    /*     123.0f, 232.0f, 10.f,    1.0f, 1.0f, 1.0f,    0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 0.0f,
     */

    private final int POS_SIZE = 3;
    private final int SCALE_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORD_SIZE = 2;

    private final int POS_OFFSET = 0 * JMath.sizeof(DataType.FLOAT);
    private final int SCALE_OFFSET = 3 * JMath.sizeof(DataType.FLOAT);
    private final int COLOR_OFFSET = 6 * JMath.sizeof(DataType.FLOAT);
    private final int TEX_COORD_OFFSET = 10 * JMath.sizeof(DataType.FLOAT);
    private final int VERTEX_SIZE = 12;
    private final int VERTEX_SIZE_BYTES = JMath.sizeof(DataType.FLOAT) * VERTEX_SIZE;

    private List<RenderComponent> renderables;
    private float[] vertices;
    private int[] indices;
    private FloatBuffer verticesBuffer;
    private Shader shader;
    private Renderer renderer;

    private int vaoID, vboID, eboID;

    public RenderBatch(int maxBatchSize, Renderer renderer) {
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.renderables = new ArrayList<>();

        // 4 Vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);

        // 6 indices per quad (3 per triangle)
        indices = new int[maxBatchSize * 6];
        this.renderer = renderer;
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

        glVertexAttribPointer(1, SCALE_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, SCALE_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void add(RenderComponent renderable) {
        // Get index and add renderable
        int index = renderables.size();
        renderables.add(renderable);
        // Add properties to local array
        loadVertexProperties(index);
        loadElementIndices(index);
    }

    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < renderables.size(); i++) {
            RenderComponent renderable = renderables.get(i);
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
        shader.uploadMat4f("uView", renderer.camera().getViewMatrix());
        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        // Draw the batch
        glDrawElements(GL_TRIANGLES, renderables.size() * 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();
    }

    public void loadVertexProperties(int index) {
        RenderComponent renderable = renderables.get(index);
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;

        Vec2f[] texCoords = renderable.getTexCoords();
        assert(texCoords.length == 4);

        Vec4f color = renderable.getColor();

        // Add 4 vertices with the appropriate properties to vertex array
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            // Load position
            vertices[offset] = renderable.gameObject.transform.position.x + (xAdd * renderable.gameObject.transform.scale.x);
            vertices[offset + 1] = renderable.gameObject.transform.position.y + (yAdd * renderable.gameObject.transform.scale.y);
            vertices[offset + 2] = renderable.gameObject.zIndex;

            // Load scale
            vertices[offset + 3] = renderable.gameObject.transform.scale.x;
            vertices[offset + 4] = renderable.gameObject.transform.scale.y;
            vertices[offset + 5] = 1.0f;

            // Load color
            vertices[offset + 6] = color.x;
            vertices[offset + 7] = color.y;
            vertices[offset + 8] = color.z;
            vertices[offset + 9] = color.w;

            // Load tex coords
            vertices[offset + 10] = texCoords[i].x;
            vertices[offset + 11] = texCoords[i].y;

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
}
