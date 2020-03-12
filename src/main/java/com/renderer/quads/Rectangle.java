package com.renderer.quads;

import com.renderer.RenderComponent;
import org.joml.Vector4f;

public class Rectangle extends RenderComponent {
    public Rectangle(Vector4f color) {
        init(texCoords, borderRadius, borderColor, color, borderWidth);
    }

    public Rectangle(Vector4f color, Vector4f borderRadius, Vector4f borderColor, float borderWidth) {
        init(texCoords, borderRadius, borderColor, color, borderWidth);
    }
}
