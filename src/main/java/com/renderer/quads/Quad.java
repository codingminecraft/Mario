package com.renderer.quads;

import com.component.Sprite;
import com.component.Spritesheet;
import com.dataStructure.AssetPool;
import com.renderer.RenderComponent;
import com.renderer.Texture;
import com.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Quad extends RenderComponent {
    private boolean flipX = false;
    private boolean flipY = false;
    private Sprite sprite;

    public Quad(Vector4f color, Texture texture, Vector2f[] texCoords) {
        init(texCoords, color, texture);
    }

    public Quad(Sprite sprite, Vector4f color) {
        Texture tex = AssetPool.getTexture(sprite.pictureFile);
        this.sprite = sprite;
        if (sprite.isSubsprite) {
            setTexCoords();
        }

        init(texCoords, color, tex);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        setTexCoords();
    }

    private void setTexCoords() {
        Spritesheet spritesheet = AssetPool.getSpritesheet(sprite.pictureFile);
        float overallWidth = spritesheet.overallWidth;
        float overallHeight = spritesheet.overallHeight;
        float topLeftX = ((float)(sprite.column * sprite.width) + (float)(spritesheet.spacing * sprite.column)) / overallWidth;
        float topLeftY = ((float)(sprite.row * sprite.height) + (float)(spritesheet.spacing * sprite.row)) / overallHeight;
        float width = sprite.width / overallWidth;
        float height = sprite.height / overallHeight;
        this.texture = AssetPool.getTexture(sprite.pictureFile);

        // Set normalized texture coordinates
        texCoords[0].x = topLeftX + width;
        texCoords[0].y = topLeftY;

        texCoords[1].x = topLeftX + width;
        texCoords[1].y = topLeftY + height;

        texCoords[2].x = topLeftX;
        texCoords[2].y = topLeftY + height;

        texCoords[3].x = topLeftX;
        texCoords[3].y = topLeftY;
        this.isDirty = true;
    }

    @Override
    public RenderComponent copy() {
        return new Quad(this.color, this.texture, this.texCoords);
    }
}
