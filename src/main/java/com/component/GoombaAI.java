package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.LevelEditorScene;
import com.jade.Window;
import com.physics.Collision;

public class GoombaAI extends Component {
    private boolean goingLeft = true;
    private boolean isDead = false;

    private float speed = 40;
    private float deadAnimTime = 0.2f;

    public GoombaAI() {

    }

    @Override
    public void update(double dt) {
        if (Window.getScene() instanceof LevelEditorScene) return;

        if (!isDead) {
            if (goingLeft) {
                this.gameObject.transform.position.x -= speed * dt;
            } else {
                this.gameObject.transform.position.x += speed * dt;
            }
        } else {
            if (deadAnimTime <= 0) {
                Window.getScene().deleteGameObject(gameObject);
            }
            deadAnimTime -= dt;
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == Collision.CollisionSide.LEFT) {
            if (coll.gameObject.getComponent(PlayerController.class) != null) coll.gameObject.getComponent(PlayerController.class).damage();

            goingLeft = false;
        } else if (coll.side == Collision.CollisionSide.TOP && coll.gameObject.getComponent(PlayerController.class) != null) {
            coll.gameObject.getComponent(Rigidbody.class).acceleration.y = 7000;
            die(true);
        } else if (coll.side == Collision.CollisionSide.RIGHT) {
            if (coll.gameObject.getComponent(PlayerController.class) != null) coll.gameObject.getComponent(PlayerController.class).damage();

            goingLeft = true;
        }
    }

    public void die(boolean spriteAnimation) {
        if (isDead) return;
        if (spriteAnimation) {
            isDead = true;
            gameObject.getComponent(AnimationMachine.class).trigger("StartSquash");
        } else {
            isDead = true;
            gameObject.transform.scale.y *= -1;
        }
    }

    @Override
    public Component copy() {
        return new GoombaAI();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("GoombaAI", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static GoombaAI deserialize() {
        Parser.consumeEndObjectProperty();

        return new GoombaAI();
    }
}
