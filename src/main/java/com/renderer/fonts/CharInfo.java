package com.renderer.fonts;

import org.joml.Vector2f;

public class CharInfo {

    private final int sourceX;
    private final int sourceY;

    private final int width;
    private final int height;

    private Vector2f[] texCoords = new Vector2f[4];

    public CharInfo(int sourceX, int sourceY, int width, int height) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.width = width;
        this.height = height;
    }

    public void makeTexCoords(int overallWidth, int overallHeight) {
        float normalWidth = (float)width / (float)overallWidth;
        float normalHeight = (float)height / (float)overallHeight;
        Vector2f topLeft = new Vector2f((float)sourceX / (float)overallWidth, (float)(sourceY - height) / (float)overallHeight);
        Vector2f topRight = new Vector2f(topLeft.x + normalWidth, topLeft.y);
        Vector2f bottomLeft = new Vector2f(topLeft.x, topLeft.y + normalHeight);
        Vector2f bottomRight = new Vector2f(topLeft.x + normalWidth, topLeft.y + normalHeight);

        this.texCoords= new Vector2f[] { topRight, bottomRight, bottomLeft, topLeft };
    }

    public Vector2f[] getTexCoords() {
        assert this.texCoords != null : "Texture coordinates must be initialized for all characters in a FontTexture.";
        return this.texCoords;
    }

    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
