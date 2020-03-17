package com.renderer.quads;

import com.jade.Window;
import com.renderer.UIRenderComponent;
import com.renderer.fonts.FontTexture;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.w3c.dom.css.Rect;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

public class Label {
    private FontTexture fontTexture;
    private List<Rectangle> rects;
    private String text;

    public Label(FontTexture fontTexture, String text, Vector2f position) {
        this.fontTexture = fontTexture;
        rects = new ArrayList<>();
        this.text = text;

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
    }

    public List<Rectangle> getRenderComponents() {
        return this.rects;
    }

    public void setPosX(float x) {
        float currentX = x;

        for (int i=0; i < text.length(); i++) {
            Rectangle rect = rects.get(i);
            rect.setPosX(currentX);

            currentX += rect.getWidth();
        }
    }

    public void setPosY(float y) {
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
