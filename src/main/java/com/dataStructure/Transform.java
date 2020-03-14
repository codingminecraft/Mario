package com.dataStructure;

import com.file.Parser;
import com.file.Serialize;
import com.util.JMath;
import org.joml.Vector2f;

public class Transform extends Serialize {
    public Vector2f position;
    public Vector2f scale;
    public float rotation;

    public Transform(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f(1.0f, 1.0f);
        this.rotation = 0.0f;
    }

    public Transform copy() {
        Transform transform = new Transform(JMath.copy(this.position));
        transform.scale = JMath.copy(this.scale);
        transform.rotation = this.rotation;
        return transform;
    }

    public static void copyValues(Transform from, Transform to) {
        to.position.x = from.position.x;
        to.position.y = from.position.y;
        to.scale.x = from.scale.x;
        to.scale.y = from.scale.y;
        to.rotation = from.rotation;
    }

    @Override
    public String toString() {
        return "Position (" + position.x + ", " + position.y + ")";
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Transform", tabSize));

        builder.append(beginObjectProperty("Position", tabSize + 1));
        builder.append(JMath.serialize(position, tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        builder.append(beginObjectProperty("Scale", tabSize + 1));
        builder.append(JMath.serialize(scale, tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        builder.append(addFloatProperty("rotation", rotation, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Transform deserialize() {
        Parser.consumeBeginObjectProperty("Transform");
        Parser.consumeBeginObjectProperty("Position");
        Vector2f position = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();

        Parser.consume(',');
        Parser.consumeBeginObjectProperty("Scale");
        Vector2f scale = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();

        Parser.consume(',');
        float rotation = Parser.consumeFloatProperty("rotation");
        Parser.consumeEndObjectProperty();

        Transform t = new Transform(position);
        t.scale = scale;
        t.rotation = rotation;

        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform other = (Transform)o;
        return other.position.x == this.position.x && other.position.y == this.position.y &&
                other.scale.x == this.scale.x && other.scale.y == this.scale.y &&
                other.rotation == this.rotation;
    }
}
