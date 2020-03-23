package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.KeyListener;
import com.physics.Collision;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private AnimationMachine machine = null;
    private Rigidbody rb = null;
    private SpriteRenderer sprite = null;
    private boolean onGround = true;

    @Override
    public Component copy() {
        return new PlayerController();
    }

    @Override
    public void start() {
        this.machine = gameObject.getComponent(AnimationMachine.class);
        this.rb = gameObject.getComponent(Rigidbody.class);
        this.sprite = gameObject.getComponent(SpriteRenderer.class);
    }

    @Override
    public void update(double dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            rb.acceleration.x = 1800;
            if (gameObject.transform.scale.x < 0) {
                gameObject.transform.scale.x *= -1;
            }
            if (onGround) {
                machine.trigger("StartRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            rb.acceleration.x = -1800;
            if (gameObject.transform.scale.x > 0) {
                gameObject.transform.scale.x *= -1;
            }
            if (onGround) {
                machine.trigger("StartRunning");
            }
        } else if (onGround) {
            rb.acceleration.x = 0;
            machine.trigger("StartIdle");
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && onGround) {
            onGround = false;
            rb.acceleration.y = 10200;
            machine.trigger("StartJumping");
        } else {
            rb.acceleration.y = 0;
        }
    }

    @Override
    public void collision(Collision collision) {
        if (collision.side == Collision.CollisionSide.BOTTOM) {
            onGround = true;
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
