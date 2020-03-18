package com.renderer.quads;

import com.jade.Window;
import com.renderer.fonts.FontTexture;
import com.util.Constants;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Label {
    private FontTexture fontTexture;
    private List<Rectangle> rects;
    private String text;
    private float width;
    private Vector2f position;

    public Label(FontTexture fontTexture, String text, Vector2f position) {
        this.fontTexture = fontTexture;
        rects = new ArrayList<>();
        this.text = text;

        this.position = position;
        float currentX = position.x;
        float currentY = position.y;
        for (char c : text.toCharArray()) {
            Rectangle rect = new Rectangle(fontTexture.getTexture(), fontTexture.getTexCoords(c));
            rect.setWidth(fontTexture.getWidthOf(c));
            rect.setHeight(fontTexture.getLineHeight());
            rect.setPosX(currentX);
            rect.setPosY(currentY);

            currentX += fontTexture.getWidthOf(c);
            rects.add(rect);
        }
        this.width = currentX - position.x;
    }

    public void setText(String text) {
        for (int i=0; i < text.length(); i++) {
            if (i < rects.size()) {
                Rectangle rect = rects.get(i);
                rect.setTexCoords(fontTexture.getTexCoords(text.charAt(i)));
                rect.setWidth(fontTexture.getWidthOf(text.charAt(i)));
            } else {
                Rectangle rect = new Rectangle(fontTexture.getTexture(), fontTexture.getTexCoords(text.charAt(i)));
                rect.setWidth(fontTexture.getWidthOf(text.charAt(i)));
                rect.setHeight(fontTexture.getLineHeight());
                rects.add(rect);
                Window.getScene().addRenderComponent(rect);
            }
        }
        this.text = text;
        this.width = Constants.DEFAULT_FONT_TEXTURE.getWidthOf(this.text);
        this.setPosX(this.position.x);
        this.setPosY(this.position.y);
    }

    public List<Rectangle> getRenderComponents() {
        return this.rects;
    }

    public String getText() {
        return this.text;
    }

    public float getWidth() {
        return this.width;
    }

    public void setPosX(float x) {
        this.position.x = x;
        float currentX = x;

        for (int i=0; i < text.length(); i++) {
            Rectangle rect = rects.get(i);
            rect.setPosX(currentX);

            currentX += rect.getWidth();
        }
    }

    public void setPosY(float y) {
        this.position.y = y;
        for (Rectangle rect : rects) {
            rect.setPosY(y);
        }
    }

    public void setZIndex(int z) {
        for (Rectangle rect : rects) {
            rect.setZIndex(z);
        }
    }
}
