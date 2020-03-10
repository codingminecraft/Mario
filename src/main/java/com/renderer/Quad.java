package com.renderer;

import com.dataStructure.AssetPool;
import com.sun.javafx.geom.Vec4f;
import com.util.JMath;
import com.util.enums.DataType;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Quad extends RenderComponent {
    private Vec4f color;
    private int vaoID, vboID, eboID;
    private Shader shader;

    private float[] verts;
    /* Example vertex, may abstract this later
    {
            // positions         // Texture coords
            0.5f,  0.5f, 1.0f,   1.0f, 1.0f,  // top right
            0.5f, -0.5f, 1.0f,   1.0f, 0.0f,  // bottom right
            -0.5f, -0.5f, 1.0f,   0.0f, 0.0f,  // bottom left
            -0.5f,  0.5f, 1.0f,   0.0f, 1.0f,  // top left
    };*/

    private int[] indices = {
            3, 2, 0,
            0, 2, 1
    };

    public Quad(Vec4f color) {
        this.color = color;
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.verts = new float[] {
                // positions           // Color
                0.5f, 0.5f, 1.0f,      color.x, color.y, color.z, color.w,  // top right
                0.5f, -0.5f, 1.0f,     color.x, color.y, color.z, color.w,  // bottom right
                -0.5f, -0.5f, 1.0f,    color.x, color.y, color.z, color.w,  // bottom left
                -0.5f, 0.5f, 1.0f,     color.x, color.y, color.z, color.w,  // top left
        };
    }

    @Override
    public void init() {
        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a FloatBuffer of vertices
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(verts.length);
        verticesBuffer.put(verts).flip();

        // Create an IntBuffer of the indices
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        // Create a Buffer Object and upload the vertices buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // Create another buffer object for the indices, then upload the indices
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Enable the buffer to know which coords are positions and which ones are
        // colors...
        glVertexAttribPointer(0, 3, GL_FLOAT, false, JMath.sizeof(DataType.FLOAT) * 7, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, JMath.sizeof(DataType.FLOAT) * 7, 4 * 3);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void render() {
        // Use our program
        shader.use();

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw a quad of 6 elements
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();
    }
}
