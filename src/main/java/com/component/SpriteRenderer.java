package com.component;

import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.jade.Component;
import com.renderer.Shader;
import com.renderer.quads.Quad;
import com.util.Constants;
import com.util.JMath;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    public Sprite sprite;
    public Vector4f color = JMath.copy(Constants.COLOR_WHITE);
    public Shader shader = AssetPool.getShader("assets/shaders/default.glsl");

    private boolean dirty = false;
    private Transform lastTransform;
    private int lastSpriteIndex;
    private String lastSpritePictureFile;
    private Vector4f lastColor;
    private Quad quad;

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;

        this.lastSpriteIndex = this.sprite.index;
        this.lastSpritePictureFile = this.sprite.pictureFile;
        this.lastColor = JMath.copy(this.color);
        this.quad = new Quad(this.sprite, this.color);
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setClean() {
        this.dirty = false;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public Quad getQuad() {
        return this.quad;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
        this.dirty = true;
    }

    @Override
    public void update(double dt) {
        if (!this.lastSpritePictureFile.equals(this.sprite.pictureFile)) {
            this.dirty = true;
            this.lastSpritePictureFile = this.sprite.pictureFile;
            this.quad.setSprite(sprite);
        }

        if (this.lastSpriteIndex != this.sprite.index) {
            this.dirty = true;
            this.lastSpriteIndex = this.sprite.index;
            this.quad.setSprite(sprite);
        }

        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.dirty = true;
            Transform.copyValues(this.gameObject.transform, this.lastTransform);
        }

        if (!this.lastColor.equals(this.color)) {
            this.dirty = true;
            JMath.copyValues(this.color, this.lastColor);
            this.quad.setColor(this.color);
        }
    }

    @Override
    public Component copy() {
        return new SpriteRenderer((Sprite)this.sprite.copy());
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
