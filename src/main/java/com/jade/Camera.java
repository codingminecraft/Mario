package com.jade;

import com.dataStructure.Vector2;
import com.dataStructure.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private boolean isPerspective = false;
    private Matrix4f projectionMatrix, viewMatrix;
    private float fov = 45;
    private float aspect = 0.0f;

    private Vector2 position;
    public Vector3f vec3Position;

    public Camera(Vector2 position) {
        this.position = position;
        this.vec3Position = new Vector3f(position.x, position.y, 10.0f);
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.caluclateAspect();
        this.adjustPerspective();
    }

    public Vector2 position() {
        return this.position;
    }

    private void caluclateAspect() {
        this.aspect = (float)Window.getWindow().getWidth() / (float)Window.getWindow().getHeight();
    }

    public void adjustPerspective() {
        if (isPerspective) {
            projectionMatrix = projectionMatrix.perspective(fov, (float) Window.getWindow().getWidth() / (float) Window.getWindow().getHeight(),
                    0.1f, 100.0f);
        } else {
            projectionMatrix.setOrtho(-1.0f, 1.0f, -1.0f, 1.0f, 0.1f, 100.0f);
            projectionMatrix.ortho(0.0f, 32.0f * 32.0f, 0.0f, 32.0f * 18.0f, 0.1f, 100.0f);
        }
    }

    public Vector3f getVec3Position() {
        this.vec3Position.x = position.x;
        this.vec3Position.y = position.y;
        return this.vec3Position;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 0.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
