package com.dataStructure;

import com.file.Parser;
import com.file.Serialize;

public class Vector3 extends Serialize {
    public float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector2 vec2, float z) {
        this.x = vec2.x;
        this.y = vec2.y;
        this.z = z;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public void clear() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3 copy() {
        return new Vector3(this.x, this.y, this.z);
    }

    public void invert() {
        this.x *= -1;
        this.y *= -1;
        this.z *= -1;
    }

    public float magnitude() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float squareMagnitude() {
        return x * x + y * y + z * z;
    }

    public void normalize() {
        float l = magnitude();
        if (l > 0) {
            x /= l;
            y /= l;
            z /= l;
        }
    }

    public void times(float val) {
        this.x *= val;
        this.y *= val;
        this.z *= val;
    }

    public void plus(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void plusScaled(Vector3 vec, float scale) {
        this.x += (vec.x * scale);
        this.y += (vec.y * scale);
        this.z += (vec.z * scale);
    }

    public void componentProduct(Vector3 vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
    }

    public float dot(Vector3 vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    public void minusEqual(Vector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void plusEqual(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void crossProduct(Vector3 vec) {
        this.x = this.y * vec.z - this.z * vec.y;
        this.y = this.z * vec.x - this.x * vec.z;
        this.z = this.x * vec.y - this.y * vec.x;
    }

    public static Vector3 crossProduct(Vector3 vec1, Vector3 vec2) {
        return new Vector3 (
                vec1.y * vec2.z - vec1.z * vec2.y,
                vec1.z * vec2.x - vec1.x * vec2.z,
                vec1.x * vec2.y - vec1.y * vec2.x
        );
    }

    public static Vector3 scale(Vector3 vec, float scale) {
        return new Vector3(vec.x * scale, vec.y * scale, vec.z * scale);
    }

    public static float dot(Vector3 vec1, Vector3 vec2) {
        return vec1.dot(vec2);
    }

    public static Vector3 add(Vector3 vec1, Vector3 vec2) {
        return new Vector3(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
    }

    public static Vector3 minus(Vector3 vec1, Vector3 vec2) {
        return new Vector3(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(addFloatProperty("x", x, tabSize, true, true));
        builder.append(addFloatProperty("y", y, tabSize, true, true));
        builder.append(addFloatProperty("z", z, tabSize, true, false));

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Vector3<" + this.x + ", " + this.y + ", " + this.z + ">";
    }

    public static Vector3 deserialize() {
        float x = Parser.consumeFloatProperty("x");
        Parser.consume(',');
        float y = Parser.consumeFloatProperty("y");
        Parser.consume(',');
        float z = Parser.consumeFloatProperty("z");

        return new Vector3(x, y, z);
    }
}
