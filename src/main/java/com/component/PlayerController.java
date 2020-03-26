package com.component;

import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.*;
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
    private boolean isDead = false;
    private float maxDeathY = 0;
    private float deathYJumpHeight = 20;
    private boolean movingUp = true;

    private float immunityTime = 1f;
    private float immunityLeft = 0.0f;
    private float flashLeft = 0.1f;
    private float flashTime = 0.1f;

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
        } else if (type == PlayerType.SMALL) {
            this.type = PlayerType.SMALL;
            this.machine.trigger("StartSmall");
            this.gameObject.transform.scale.y = 32;
            bounds.setHeight(31);
            immunityLeft = immunityTime;
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
        if (Window.getScene() instanceof LevelEditorScene) return;

        if (immunityLeft > 0 && flashLeft < 0) {
            if (sprite.color.w < 1) {
                sprite.color.w = 1;
            } else {
                sprite.color.w = 0.5f;
            }
            flashLeft = flashTime;
        } else if (sprite.color.w < 1) {
            sprite.color.w = 1;
        }

        if (isDead) {
            if (movingUp && gameObject.transform.position.y < maxDeathY) {
                rb.acceleration.y = 3000;
            } else {
                rb.acceleration.y = -100;
                movingUp = false;
            }
            return;
        }

        if (this.camera.position().x < this.gameObject.transform.position.x - Constants.CAMERA_OFFSET_X) {
            this.camera.position().x = this.gameObject.transform.position.x - Constants.CAMERA_OFFSET_X;
        }

        if (this.gameObject.transform.position.y < Constants.CAMERA_OFFSET_Y_1 && camera.position().y != Constants.CAMERA_OFFSET_Y_2) {
            Window.getWindow().setColor(Constants.COLOR_BLACK);
            this.camera.position().y = Constants.CAMERA_OFFSET_Y_2;
        } else if (this.gameObject.transform.position.y > Constants.CAMERA_OFFSET_Y_1 && camera.position().y != Constants.CAMERA_OFFSET_Y_1) {
            Window.getWindow().setColor(Constants.SKY_COLOR);
            this.camera.position().y = Constants.CAMERA_OFFSET_Y_1;
        }

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

        immunityLeft -= dt;
        flashLeft -= dt;
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

    public void damage() {
        if (immunityLeft > 0) return;

        if (type == PlayerType.SMALL) {
            die();
        } else if (type == PlayerType.BIG || type == PlayerType.FIRE) {
            setState(PlayerType.SMALL);
        }
    }

    public void die() {
        machine.trigger("Die");
        isDead = true;
        maxDeathY = gameObject.transform.position.y + deathYJumpHeight;
        rb.acceleration.x = 0;
        rb.velocity.x = 0;
        gameObject.getComponent(BoxBounds.class).isTrigger = true;
        gameObject.zIndex = 10;
    }

    public static PlayerController deserialize() {
        Parser.consumeEndObjectProperty();

        return new PlayerController();
    }
}
