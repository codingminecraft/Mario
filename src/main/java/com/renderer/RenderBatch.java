package com.renderer;

import com.component.SpriteRenderer;
import com.dataStructure.AssetPool;
import com.jade.GameObject;
import com.jade.Window;
import com.util.JMath;
import com.util.enums.DataType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    /*
    /*      Vertex
    /*     ======
    /*     Pos                      Color                       TexCoord         TexID
    /*     123.0f, 232.0f, 10.f,    0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 0.0f,      1
     */

    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORD_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0 * JMath.sizeof(DataType.FLOAT);
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int TEX_ID_OFFSET = TEX_COORD_OFFSET + TEX_COORD_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = JMath.sizeof(DataType.FLOAT) * VERTEX_SIZE;

    private List<SpriteRenderer> sprites;
    private List<Texture> textures;
    private float[] vertices;
    private int[] indices;
    private FloatBuffer verticesBuffer;
    private Shader shader;
    private Renderer renderer;
    private int maxBatchSize;

    private int vaoID, vboID, eboID;

    public int zIndex;
    public boolean hasRoom = true;

    public RenderBatch(int maxBatchSize, Renderer renderer, int zIndex) {
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.textures = new ArrayList<>();
        this.sprites = new ArrayList<>();
        this.maxBatchSize = maxBatchSize;

        // 4 Vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);

        // 6 indices per quad (3 per triangle)
        indices = new int[maxBatchSize * 6];
        this.renderer = renderer;

        this.zIndex = zIndex;

        generateIndices();
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

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void add(SpriteRenderer spr) {
        // Get index and add renderable
        int index = this.sprites.size();
        this.sprites.add(spr);

        // If renderable has texture add it if it is needed to this batch's textures
        if (spr.getQuad().getTexture() != null) {
            if (!textures.contains(spr.getQuad().getTexture())) {
                textures.add(spr.getQuad().getTexture());
            }
        }

        // Add properties to local array
        loadVertexProperties(index);

        if (sprites.size() >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 7;
    }

    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < sprites.size(); i++) {
            SpriteRenderer spr = sprites.get(i);
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
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
        // Upload all the textures
        for (int i=0; i < textures.size(); i++) {
            shader.uploadTexture("TEX_" + (i + 1), i + 1);
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadFloat("uAspect", Window.getWindow().getAsepct());

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        // Draw the batch
        glDrawElements(GL_TRIANGLES, sprites.size() * 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        for (Texture tex : textures) {
            tex.unbind();
        }
        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();
    }

    public void deleteVertexProperties(int index) {
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;

        for (int i=0; i < 4; i++) {
            // Load position
            vertices[offset] = 0.0f;
            vertices[offset + 1] = 0.0f;
            vertices[offset + 2] = 0.0f;

            // Load color
            vertices[offset + 3] = 0.0f;
            vertices[offset + 4] = 0.0f;
            vertices[offset + 5] = 0.0f;
            vertices[offset + 6] = 0.0f;

            // Load tex coords
            vertices[offset + 7] = 0.0f;
            vertices[offset + 8] = 0.0f;

            // Load tex id
            vertices[offset + 9] = 0.0f;

            offset += VERTEX_SIZE;
        }
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites.get(index);
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;

        Vector4f color = sprite.getQuad().getColor();

        Vector2f[] texCoords = sprite.getQuad().getTexCoords();
        assert(texCoords.length == 4);

        Texture tex = sprite.getQuad().getTexture();
        int texSlot = 0;
        if (tex != null) {
            for (int i=0; i < textures.size(); i++) {
                if (textures.get(i) == tex) {
                    texSlot = i + 1;
                    break;
                }
            }
        }

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
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);
            vertices[offset + 2] = sprite.gameObject.zIndex;

            // Load color
            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            // Load tex coords
            vertices[offset + 7] = texCoords[i].x;
            vertices[offset + 8] = texCoords[i].y;

            // Load tex id
            vertices[offset + 9] = texSlot;


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

    public void generateIndices() {
        for (int i=0; i < maxBatchSize; i++) {
            this.loadElementIndices(i);
        }
    }

    @Override
    public int compareTo(RenderBatch batch) {
        return Integer.compare(batch.zIndex, this.zIndex);
    }
}
