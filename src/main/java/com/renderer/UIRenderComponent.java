package com.renderer;

import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class UIRenderComponent {
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

    protected Vector2f position, size;
    protected int zIndex = 0;
    public boolean isDirty = false;

    public void init(Vector2f[] texCoords, Vector4f borderRadius, Vector4f borderColor, Vector4f color, float borderWidth) {
        this.size = new Vector2f();
        this.position = new Vector2f();
        this.texCoords = texCoords;
        this.borderRadius = borderRadius;
        this.borderColor = new Vector4f(borderColor.x, borderColor.y, borderColor.z, borderColor.w);
        this.color = new Vector4f(color.x, color.y, color.z, color.w);
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
        return this.size.x;
    }
    public float getHeight() {
        return this.size.y;
    }
    public float getPosX() {
        return this.position.x;
    }
    public float getPosY() {
        return this.position.y;
    }
    public Vector2f getPosition() {
        return this.position;
    }
    public Vector2f getSize() {
        return this.size;
    }
    public int getZIndex() {
        return this.zIndex;
    }

    public void setColor(float r, float g, float b, float a) {
        this.isDirty = true;
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
    }
    public void setColor(Vector4f color) {
        this.color.x = color.x;
        this.color.y = color.y;
        this.color.z = color.z;
        this.color.w = color.w;
        this.isDirty = true;
    }
    public void setTexCoords(Vector2f[] texCoords) {
        this.isDirty = true;
        this.texCoords = texCoords;
    }
    public void setBorderRadius(Vector4f radius) {
        this.isDirty = true;
        this.borderRadius = radius;
    }
    public void setBorderRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.isDirty = true;
        this.borderRadius.x = topLeft;
        this.borderRadius.y = topRight;
        this.borderRadius.z = bottomLeft;
        this.borderRadius.w = bottomRight;
    }
    public void setBorderRadius(float val) {
        this.isDirty = true;
        this.borderRadius.x = val;
        this.borderRadius.y = val;
        this.borderRadius.z = val;
        this.borderRadius.w = val;
    }
    public void setBorderColor(Vector4f color) {
        this.isDirty = true;
        this.borderColor = color;
    }
    public void setBorderColor(float r, float g, float b, float a) {
        this.isDirty = true;
        this.borderColor.x = r;
        this.borderColor.y = g;
        this.borderColor.z = b;
        this.borderColor.w = a;
    }
    public void setBorderWidth(float width) {
        this.isDirty = true;
        this.borderWidth = width;
    }
    public void setWidth(float width) {
        this.isDirty = true;
        this.size.x = width;
    }
    public void setHeight(float height) {
        this.isDirty = true;
        this.size.y = height;
    }
    public void setPosX(float x) {
        this.isDirty = true;
        this.position.x = x;
    }
    public void setPosY(float y) {
        this.isDirty = true;
        this.position.y = y;
    }
    public void setZIndex(int zIndex) {
        this.isDirty = true;
        this.zIndex = zIndex;
    }
    public void setPosition(Vector2f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.isDirty = true;
    }
    public void setSize(Vector2f size) {
        this.size.x = size.x;
        this.size.y = size.y;
        this.isDirty = true;
    }
}
