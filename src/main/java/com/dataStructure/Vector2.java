package com.dataStructure;

import com.file.Parser;
import com.file.Serialize;

public class Vector2 extends Serialize {
    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public void clear() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }

    public void invert() {
        this.x *= -1;
        this.y *= -1;
    }

    public float magnitude() {
        return (float)Math.sqrt(x * x + y * y);
    }

    public float squareMagnitude() {
        return x * x + y * y;
    }

    public void normalize() {
        float l = magnitude();
        if (l > 0) {
            x /= l;
            y /= l;
        }
    }

    public void times(float val) {
        this.x *= val;
        this.y *= val;
    }

    public void plus(Vector2 vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    public void plusScaled(Vector2 vec, float scale) {
        this.x += (vec.x * scale);
        this.y += (vec.y * scale);
    }

    public void componentProduct(Vector2 vec) {
        this.x *= vec.x;
        this.y *= vec.y;
    }

    public float dot(Vector2 vec) {
        return x * vec.x + y * vec.y;
    }

    public static Vector2 scale(Vector2 vec, float scale) {
        return new Vector2(vec.x * scale, vec.y * scale);
    }

    public static float dot(Vector2 vec1, Vector2 vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y;
    }

    public static Vector2 add(Vector2 vec1, Vector2 vec2) {
        return new Vector2(vec1.x + vec2.x, vec1.y + vec2.y);
    }

    public static Vector2 minus(Vector2 vec1, Vector2 vec2) {
        return new Vector2(vec1.x - vec2.x, vec1.y - vec2.y);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(addFloatProperty("x", x, tabSize, true, true));
        builder.append(addFloatProperty("y", y, tabSize, true, false));

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Vector2<" + this.x + ", " + this.y + ">";
    }

    public static Vector2 deserialize() {
        float x = Parser.consumeFloatProperty("x");
        Parser.consume(',');
        float y = Parser.consumeFloatProperty("y");

        return new Vector2(x, y);
    }
}
