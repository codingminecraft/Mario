package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.KeyListener;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private AnimationMachine machine = null;
    private Rigidbody rb = null;
    private BoxBounds bounds = null;

    @Override
    public Component copy() {
        return new PlayerController();
    }

    @Override
    public void start() {
        this.machine = gameObject.getComponent(AnimationMachine.class);
        this.rb = gameObject.getComponent(Rigidbody.class);
        this.bounds = gameObject.getComponent(BoxBounds.class);
    }

    @Override
    public void update(double dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            rb.acceleration.x = 1800;
            machine.trigger("StartRunning");
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            rb.acceleration.x = -1800;
            machine.trigger("StartRunning");
        } else {
            rb.acceleration.x = 0;
            machine.trigger("StartIdle");
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && bounds.onGround) {
            bounds.onGround = false;
            rb.acceleration.y = 10200;
            machine.trigger("StartJumping");
        } else {
            rb.acceleration.y = 0;
        }
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("PlayerController", tabSize));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static PlayerController deserialize() {
        Parser.consumeEndObjectProperty();

        return new PlayerController();
    }
}
