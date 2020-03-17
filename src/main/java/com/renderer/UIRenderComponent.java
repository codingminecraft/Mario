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
    protected Texture texture = null;

    protected float borderWidth = 0;

    protected Vector2f position, size;
    protected int zIndex = 0;
    public boolean isDirty = false;

    public void init(Vector2f[] texCoords, Vector4f borderRadius, Vector4f borderColor, Vector4f color, float borderWidth, Texture texture) {
        this.size = new Vector2f();
        this.position = new Vector2f();
        this.texCoords = texCoords;
        this.borderRadius = borderRadius;
        this.borderColor = new Vector4f(borderColor.x, borderColor.y, borderColor.z, borderColor.w);
        this.color = new Vector4f(color.x, color.y, color.z, color.w);
        this.borderWidth = borderWidth;
        this.texture = texture;

        assert texCoords.length == 4 : "Rectangle must have 4 texture coordinates.";
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
    public Texture getTexture() {
        return this.texture;
    }

    public void setColor(float r, float g, float b, float a) {
        if (r != this.color.x || g != this.color.y || b != this.color.z || a != this.color.w) {
            this.isDirty = true;
            this.color.x = r;
            this.color.y = g;
            this.color.z = b;
            this.color.w = a;
        }
    }
    public void setColor(Vector4f color) {
        if (color.x != this.color.x || color.y != this.color.y || color.z != this.color.z || color.w != this.color.w) {
            this.color.x = color.x;
            this.color.y = color.y;
            this.color.z = color.z;
            this.color.w = color.w;
            this.isDirty = true;
        }
    }
    public void setTexCoords(Vector2f[] texCoords) {
        this.isDirty = false;
        for (int i=0; i < texCoords.length; i++) {
            if (texCoords[i].x != this.texCoords[i].x || texCoords[i].y != this.texCoords[i].y) {
                this.isDirty = true;
                break;
            }
        }
        this.texCoords = texCoords;
    }
    public void setBorderRadius(Vector4f radius) {
        if (radius.x != this.borderRadius.x || radius.y != this.borderRadius.y || radius.z != this.borderRadius.z || radius.w != this.borderRadius.w) {
            this.isDirty = true;
            this.borderRadius = radius;
        }
    }
    public void setBorderRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        if (topLeft != this.borderRadius.x || topRight != this.borderRadius.y || bottomLeft != this.borderRadius.z || bottomRight != this.borderRadius.w) {
            this.isDirty = true;
            this.borderRadius.x = topLeft;
            this.borderRadius.y = topRight;
            this.borderRadius.z = bottomLeft;
            this.borderRadius.w = bottomRight;
        }
    }
    public void setBorderRadius(float val) {
        if (val != this.borderRadius.x || val != this.borderRadius.y || val != this.borderRadius.z || val != this.borderRadius.w) {
            this.isDirty = true;
            this.borderRadius.x = val;
            this.borderRadius.y = val;
            this.borderRadius.z = val;
            this.borderRadius.w = val;
        }
    }
    public void setBorderColor(Vector4f color) {
        if (color.x != this.borderColor.x || color.y != this.borderColor.y || color.z != this.borderColor.z || color.w != this.borderColor.w) {
            this.isDirty = true;
            this.borderColor = color;
        }
    }
    public void setBorderColor(float r, float g, float b, float a) {
        if (r != this.borderColor.x || g != this.borderColor.y || b != this.borderColor.z || a != this.borderColor.w) {
            this.isDirty = true;
            this.borderColor.x = r;
            this.borderColor.y = g;
            this.borderColor.z = b;
            this.borderColor.w = a;
        }
    }
    public void setBorderWidth(float width) {
        if (width != this.borderWidth) {
            this.isDirty = true;
            this.borderWidth = width;
        }
    }
    public void setWidth(float width) {
        if (width != this.size.x) {
            this.isDirty = true;
            this.size.x = width;
        }
    }
    public void setHeight(float height) {
        if (height != this.size.y) {
            this.isDirty = true;
            this.size.y = height;
        }
    }
    public void setPosX(float x) {
        if (x != this.position.x) {
            this.isDirty = true;
            this.position.x = x;
        }
    }
    public void setPosY(float y) {
        if (y != this.position.y) {
            this.isDirty = true;
            this.position.y = y;
        }
    }
    public void setZIndex(int zIndex) {
        if (zIndex != this.zIndex) {
            this.isDirty = true;
            this.zIndex = zIndex;
        }
    }
    public void setPosition(Vector2f position) {
        if (this.position.x != position.x || this.position.y != position.y) {
            this.position.x = position.x;
            this.position.y = position.y;
            this.isDirty = true;
        }
    }
    public void setSize(Vector2f size) {
        if (this.size.x != size.x || this.size.y != size.y) {
            this.size.x = size.x;
            this.size.y = size.y;
            this.isDirty = true;
        }
    }
}
