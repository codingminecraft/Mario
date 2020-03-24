package com.component;

import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.Camera;
import com.jade.Component;
import com.jade.KeyListener;
import com.jade.Window;
import com.physics.Collision;
import com.util.Constants;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private AnimationMachine machine = null;
    private Rigidbody rb = null;
    private SpriteRenderer sprite = null;
    private boolean onGround = true;
    private BoxBounds bounds = null;
    private Camera camera;

    public PlayerType type = PlayerType.SMALL;

    public void setState(PlayerType type) {
        if (type == PlayerType.BIG) {
            if (this.type == PlayerType.SMALL) {
                this.type = PlayerType.BIG;
                this.machine.trigger("StartBig");
                this.gameObject.transform.scale.y = 64;
                bounds.setHeight(63);
            } else if (this.type == PlayerType.BIG) {
                System.out.println("Collect 100 points!");
            }
        } else if (type == PlayerType.FIRE) {
            if (this.type == PlayerType.BIG) {
                this.type = PlayerType.FIRE;
                this.machine.trigger("StartFire");
            } else if (this.type == PlayerType.FIRE) {
                System.out.println("Collect 100 points!");
            }
        }
    }

    @Override
    public Component copy() {
        return new PlayerController();
    }

    @Override
    public void start() {
        this.machine = gameObject.getComponent(AnimationMachine.class);
        this.rb = gameObject.getComponent(Rigidbody.class);
        this.sprite = gameObject.getComponent(SpriteRenderer.class);
        this.bounds = gameObject.getComponent(BoxBounds.class);
        this.camera = Window.getScene().camera;
    }

    @Override
    public void update(double dt) {
        if (this.camera.position().x < this.gameObject.transform.position.x - Constants.CAMERA_OFFSET_X)
            this.camera.position().x = this.gameObject.transform.position.x - Constants.CAMERA_OFFSET_X;

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
            rb.acceleration.y = 12200;
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
