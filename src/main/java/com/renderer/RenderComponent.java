package com.renderer;

import com.jade.GameObject;
import com.jade.Object;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class RenderComponent {
    protected Vector2f[] texCoords = new Vector2f[] {
        new Vector2f(1.0f, 1.0f),
        new Vector2f(1.0f, 0.0f),
        new Vector2f(0.0f, 0.0f),
        new Vector2f(0.0f, 1.0f)
    };
    protected Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    protected Vector4f borderRadius = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
    protected Vector4f borderColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    protected float borderWidth = 0;

    public Object gameObject;
    public boolean isDirty = false;

    public void init(Vector2f[] texCoords, Vector4f borderRadius, Vector4f borderColor, Vector4f color, float borderWidth) {
        this.texCoords = texCoords;
        this.borderRadius = borderRadius;
        this.borderColor = borderColor;
        this.color = color;
        this.borderWidth = borderWidth;
    }

    public Vector4f getColor() {
        return this.color;
    }
    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
    public Vector4f getBorderRadius() {
        return this.borderRadius;
    }
    public Vector4f getBorderColor() {
        return this.borderColor;
    }
    public float getBorderWidth() {
        return this.borderWidth;
    }
    public float getWidth() {
        return this.gameObject.transform.scale.x;
    }
    public float getHeight() {
        return this.gameObject.transform.scale.y;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
    }
    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }
    private void setBorderRadius(Vector4f radius) {
        this.borderRadius = radius;
    }
    private void setBorderRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.borderRadius.x = topLeft;
        this.borderRadius.y = topRight;
        this.borderRadius.z = bottomLeft;
        this.borderRadius.w = bottomRight;
    }
    private void setBorderRadius(float val) {
        this.borderRadius.x = val;
        this.borderRadius.y = val;
        this.borderRadius.z = val;
        this.borderRadius.w = val;
    }
    private void setBorderColor(Vector4f color) {
        this.borderColor = color;
    }
    private void setBorderColor(float r, float g, float b, float a) {
        this.borderColor.x = r;
        this.borderColor.y = g;
        this.borderColor.z = b;
        this.borderColor.w = a;
    }
    private void setBorderWidth(float width) {
        this.borderWidth = width;
    }
}
