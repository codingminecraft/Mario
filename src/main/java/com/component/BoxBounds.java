package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import org.joml.Vector2f;

public class BoxBounds extends Bounds {
    public float width, height;
    public float halfWidth, halfHeight;
    public Vector2f center = new Vector2f();
    public float xBuffer = 0.0f;
    public float yBuffer = 0.0f;

    public float enclosingRadius;
    public boolean onGround = false;

    public BoxBounds(float width, float height, boolean isStatic) {
        init(width, height, isStatic);
    }

    public void init(float width, float height, boolean isStatic) {
        this.width = width;
        this.height = height;
        this.halfWidth = this.width / 2.0f;
        this.halfHeight = this.height / 2.0f;
        this.enclosingRadius = (float)Math.sqrt((this.halfWidth * this.halfWidth) +
                (this.halfHeight * this.halfHeight));
        this.type = BoundsType.Box;
        this.isStatic = isStatic;
    }

    @Override
    public void start() {
        this.calculateCenter();
    }

    public void calculateCenter() {
        this.center.x = this.gameObject.transform.position.x + this.halfWidth + this.xBuffer;
        this.center.y = this.gameObject.transform.position.y + this.halfHeight + this.yBuffer;
    }

    public static boolean checkCollision(BoxBounds b1, BoxBounds b2) {
        b1.calculateCenter();
        b2.calculateCenter();

        float dx = b2.center.x - b1.center.x;
        float dy = b2.center.y - b1.center.y;

        float combinedHalfWidths = b1.halfWidth + b2.halfWidth;
        float combinedHalfHeights = b1.halfHeight + b2.halfHeight;

        if (Math.abs(dx) <= combinedHalfWidths) {
            return Math.abs(dy) <= combinedHalfHeights;
        }

        return false;
    }

    public void resolveCollision(BoxBounds otherBounds) {
        otherBounds.calculateCenter();
        this.calculateCenter();

        float dx = this.center.x - otherBounds.center.x;
        float dy = this.center.y - otherBounds.center.y;

        float combinedHalfWidths = otherBounds.halfWidth + this.halfWidth;
        float combinedHalfHeights = otherBounds.halfHeight + this.halfHeight;

        float overlapX = combinedHalfWidths - Math.abs(dx);
        float overlapY = combinedHalfHeights - Math.abs(dy);

        if (overlapX >= overlapY) {
            if (dy > 0) {
                // Collision on the top of the oBounds
                this.gameObject.transform.position.y = otherBounds.gameObject.transform.position.y + otherBounds.getHeight();
                this.onGround = true;
                if (this.gameObject.getComponent(Rigidbody.class).velocity.y < 0)
                    this.gameObject.getComponent(Rigidbody.class).velocity.y = 0;
            } else {
                // Collision on the bottom of the oBounds
                this.gameObject.transform.position.y = otherBounds.gameObject.transform.position.y - this.getHeight();
                if (this.gameObject.getComponent(Rigidbody.class).velocity.y > 0)
                    this.gameObject.getComponent(Rigidbody.class).velocity.y = 0;
            }
        } else {
            if (dx < 0) {
                // Collision on the left of the oBounds
                this.gameObject.transform.position.x = otherBounds.gameObject.transform.position.x - this.getWidth();
            } else {
                // Collision on the right of the oBounds
                this.gameObject.transform.position.x = otherBounds.gameObject.transform.position.x + otherBounds.getWidth();
            }
        }
    }

    @Override
    public Component copy() {
        BoxBounds bounds = new BoxBounds(width, height, isStatic);
        bounds.xBuffer = xBuffer;
        bounds.yBuffer = yBuffer;
        return bounds;
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("BoxBounds", tabSize));
        builder.append(addFloatProperty("Width", this.width, tabSize + 1, true, true));
        builder.append(addFloatProperty("Height", this.height, tabSize + 1, true, true));
        builder.append(addFloatProperty("xBuffer", this.xBuffer, tabSize + 1, true, true));
        builder.append(addFloatProperty("yBuffer", this.yBuffer, tabSize + 1, true, true));
        builder.append(addBooleanProperty("isStatic", this.isStatic, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static BoxBounds deserialize() {
        float width = Parser.consumeFloatProperty("Width");
        Parser.consume(',');
        float height = Parser.consumeFloatProperty("Height");
        Parser.consume(',');
        float xBuffer = Parser.consumeFloatProperty("xBuffer");
        Parser.consume(',');
        float yBuffer = Parser.consumeFloatProperty("yBuffer");
        Parser.consume(',');
        boolean isStatic = Parser.consumeBooleanProperty("isStatic");
        Parser.consumeEndObjectProperty();

        BoxBounds bounds =  new BoxBounds(width, height, isStatic);
        bounds.xBuffer = xBuffer;
        bounds.yBuffer = yBuffer;
        return bounds;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public boolean raycast(Vector2f position) {
        return false;
    }
}
