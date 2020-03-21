package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import org.joml.Vector2f;

public class BoxBounds extends Bounds {
    public float width, height;
    public float halfWidth, halfHeight;
    public Vector2f center = new Vector2f();
    public boolean isTrigger;
    public float xBuffer = 0.0f;
    public float yBuffer = 0.0f;

    public float enclosingRadius;

    public BoxBounds(float width, float height) {
        init(width, height, false);
    }

    public BoxBounds(float width, float height, boolean isTrigger) {
        init(width, height, isTrigger);
    }

    public void init(float width, float height, boolean isTrigger) {
        this.width = width;
        this.height = height;
        this.halfWidth = this.width / 2.0f;
        this.halfHeight = this.height / 2.0f;
        this.enclosingRadius = (float)Math.sqrt((this.halfWidth * this.halfWidth) +
                (this.halfHeight * this.halfHeight));
        this.type = BoundsType.Box;
        this.isTrigger = isTrigger;
    }

    @Override
    public void start() {
        this.calculateCenter();
    }

    public void calculateCenter() {
        this.center.x = this.gameObject.transform.position.x + this.halfWidth + this.xBuffer;
        this.center.y = this.gameObject.transform.position.y + this.halfHeight + this.yBuffer;
    }

    @Override
    public void update(double dt){

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
        if (isTrigger) return;

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

            } else {
                // Collision on the bottom of the oBounds

            }
        } else {
            if (dx < 0) {
                // Collision on the left of the oBounds

            } else {
                // Collision on the right of the oBounds

            }
        }

        System.out.println("Resolving collision!");
    }

    @Override
    public Component copy() {
        BoxBounds bounds = new BoxBounds(width, height, isTrigger);
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
        builder.append(addBooleanProperty("isTrigger", this.isTrigger, tabSize + 1, true, false));
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
        boolean isTrigger = Parser.consumeBooleanProperty("isTrigger");
        Parser.consumeEndObjectProperty();

        BoxBounds bounds =  new BoxBounds(width, height, isTrigger);
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

//    @Override
//    public void draw(Graphics2D g2) {
//        if (isSelected) {
//            g2.setColor(Color.GREEN);
//            g2.setStroke(Constants.THICK_LINE);
//            g2.draw(new Rectangle2D.Float(
//                    this.gameObject.transform.position.x + xBuffer,
//                    this.gameObject.transform.position.y + yBuffer,
//                    this.width,
//                    this.height));
//            g2.setStroke(Constants.LINE);
//        }
//    }
}
