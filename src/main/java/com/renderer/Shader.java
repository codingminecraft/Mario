package com.renderer;


import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgram;
    private int vertexID;
    private int fragmentID;
    private boolean beingUsed;

    private String fragmentSource;
    private String vertexSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+[a-zA-Z]+");
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).strip();
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).strip();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "' while compiling shader.");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "' while compiling shader.");
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void compile() {
        // Load vertex shader and compile
        vertexID = GL30.glCreateShader(GL_VERTEX_SHADER);
        GL30.glShaderSource(vertexID, vertexSource);
        GL30.glCompileShader(vertexID);

        // Check for errors in compilation
        int success = GL30.glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == 0) {
            int len = GL30.glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(GL30.glGetShaderInfoLog(vertexID, len));
            System.exit(-1);
        }

        // Load fragment shader and compile
        fragmentID = GL30.glCreateShader(GL_FRAGMENT_SHADER);
        GL30.glShaderSource(fragmentID, fragmentSource);
        GL30.glCompileShader(fragmentID);

        // Check for errors in compilation
        success = GL30.glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == 0) {
            int len = GL30.glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(GL30.glGetShaderInfoLog(fragmentID, len));
            System.exit(-1);
        }

        // Link the shaders and create their shader program
        shaderProgram = GL30.glCreateProgram();
        GL30.glAttachShader(shaderProgram, vertexID);
        GL30.glAttachShader(shaderProgram, fragmentID);
        GL30.glLinkProgram(shaderProgram);

        // Check for linking errors
        success = GL30.glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = GL30.glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tShader linking failed.");
            System.out.println(GL30.glGetProgramInfoLog(shaderProgram, len));
            System.exit(-1);
        }
    }

    public void use() {
        GL30.glUseProgram(shaderProgram);
        beingUsed = true;
    }

    public void detach() {
        GL30.glUseProgram(0);
        beingUsed = false;
    }

    public void delete() {
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
        glDeleteProgram(shaderProgram);
    }

    public void uploadVec4f(String varName, Vector4f vec4) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        glUniform4f(varLocation, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        glUniform3f(varLocation, vec3.x, vec3.y, vec3.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        glUniform2f(varLocation, vec2.x, vec2.y);
    }

    public void uploadFloat(String varName, float value) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        glUniform1f(varLocation, value);
    }

    public void uploadInt(String varName, int value) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        glUniform1i(varLocation, value);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        if (!beingUsed) this.use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(0);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
}
