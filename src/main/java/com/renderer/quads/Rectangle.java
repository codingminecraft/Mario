package com.renderer.quads;

import com.renderer.UIRenderComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Rectangle extends UIRenderComponent {

    public Rectangle(Vector4f color) {
        init(texCoords, borderRadius, borderColor, color, borderWidth);
    }

    public Rectangle(Vector4f color, Vector4f borderRadius, Vector4f borderColor, float borderWidth) {
        this.size = new Vector2f();
        init(texCoords, borderRadius, borderColor, color, borderWidth);
    }
}
