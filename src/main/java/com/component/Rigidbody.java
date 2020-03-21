package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.LevelEditorScene;
import com.jade.LevelScene;
import com.jade.Window;
import com.util.Constants;
import org.joml.Vector2f;

public class Rigidbody extends Component {
    public Vector2f velocity;
    public Vector2f acceleration;

    public Rigidbody() {
        this.velocity = new Vector2f();
        this.acceleration = new Vector2f(0, Constants.GRAVITY);
    }

    @Override
    public void update(double dt) {
        if (Window.getScene() instanceof LevelEditorScene) return;

        this.gameObject.transform.position.add(this.velocity.x * (float)dt, this.velocity.y * (float)dt);

        this.velocity.add(this.acceleration.x * (float)dt, (this.acceleration.y * (float)dt) + (Constants.GRAVITY * (float)dt));
        this.velocity.x *= 0.8f;
        this.velocity.y *= 0.99f;
    }

    @Override
    public Component copy() {
        return new Rigidbody();
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Rigidbody", tabSize));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Rigidbody deserialize() {
        Parser.consumeEndObjectProperty();

        return new Rigidbody();
    }
}
