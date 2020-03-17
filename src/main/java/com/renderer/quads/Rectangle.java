package com.renderer.quads;

import com.component.Sprite;
import com.component.Spritesheet;
import com.dataStructure.AssetPool;
import com.renderer.Texture;
import com.renderer.UIRenderComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Rectangle extends UIRenderComponent {

    public Rectangle(Vector4f color) {
        init(texCoords, borderRadius, borderColor, color, borderWidth, null);
    }

    public Rectangle(Texture texture, Vector2f[] texCoords) {
        init(texCoords, borderRadius, borderColor, color, borderWidth, texture);
    }

    public Rectangle(Sprite sprite, Vector2f size) {
        Texture tex = AssetPool.getTexture(sprite.pictureFile);
        if (sprite.isSubsprite) {
            Spritesheet spritesheet = AssetPool.getSpritesheet(sprite.pictureFile);
            float overallWidth = spritesheet.overallWidth;
            float overallHeight = spritesheet.overallHeight;
            float topLeftX = ((float)(sprite.column * sprite.width) + (float)(spritesheet.spacing * sprite.column)) / overallWidth;
            float topLeftY = ((float)(sprite.row * sprite.height) + (float)(spritesheet.spacing * sprite.row)) / overallHeight;
            float width = sprite.width / overallWidth;
            float height = sprite.height / overallHeight;

            // Set normalized texture coordinates
            texCoords[0].x = topLeftX + width;
            texCoords[0].y = topLeftY;

            texCoords[1].x = topLeftX + width;
            texCoords[1].y = topLeftY + height;

            texCoords[2].x = topLeftX;
            texCoords[2].y = topLeftY + height;

            texCoords[3].x = topLeftX;
            texCoords[3].y = topLeftY;
        }

        init(texCoords, borderRadius, borderColor, color, borderWidth, tex);
        this.setSize(size);
    }

    public Rectangle(Vector4f color, Vector4f borderRadius, Vector4f borderColor, float borderWidth) {
        this.size = new Vector2f();
        init(texCoords, borderRadius, borderColor, color, borderWidth, null);
    }
}
