package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.LevelEditorScene;
import com.jade.Window;
import com.physics.Collision;
import com.physics.Trigger;

import javax.swing.Box;

public class TurtleAI extends Component {
    private boolean goingLeft = true;
    private boolean isShell = false;
    private boolean isShellMoving = false;

    private float speed = 40;

    public TurtleAI() {

    }

    @Override
    public void update(double dt) {
        if (Window.getScene() instanceof LevelEditorScene) return;

        if (!isShell) {
            if (goingLeft) {
                this.gameObject.transform.position.x -= speed * dt;
            } else {
                this.gameObject.transform.position.x += speed * dt;
            }
        } else if (isShellMoving) {
            if (goingLeft) {
                this.gameObject.transform.position.x -= speed * dt * 5;
            } else {
                this.gameObject.transform.position.x += speed * dt * 5;
            }
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == Collision.CollisionSide.LEFT) {
            if (coll.gameObject.getComponent(PlayerController.class) != null) coll.gameObject.getComponent(PlayerController.class).damage();

            goingLeft = false;
            gameObject.transform.scale.x *= -1;
        } else if (coll.side == Collision.CollisionSide.RIGHT) {
            if (coll.gameObject.getComponent(PlayerController.class) != null) coll.gameObject.getComponent(PlayerController.class).damage();

            goingLeft = true;
            gameObject.transform.scale.x *= -1;
        } else if (coll.side == Collision.CollisionSide.TOP && !isShell && coll.gameObject.getComponent(PlayerController.class) != null) {
            turnIntoShell();
            coll.gameObject.getComponent(Rigidbody.class).acceleration.y = 7000;
        }
    }

    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(GoombaAI.class) != null) {
            trigger.gameObject.getComponent(GoombaAI.class).die(false);
        } else if (trigger.gameObject.getComponent(PlayerController.class) != null) {
            BoxBounds player = trigger.gameObject.getComponent(BoxBounds.class);
            BoxBounds me = this.gameObject.getComponent(BoxBounds.class);
            if (player.gameObject.transform.position.y < me.getCenterY()) {
                trigger.gameObject.getComponent(PlayerController.class).die();
            } else {
                isShellMoving = true;
                goingLeft = !goingLeft;
            }
        }
    }

    public void turnIntoShell() {
        this.gameObject.getComponent(AnimationMachine.class).trigger("StartSpin");
        this.gameObject.getComponent(BoxBounds.class).isTrigger = true;
        this.isShell = true;
    }

    @Override
    public Component copy() {
        return new TurtleAI();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("TurtleAI", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static TurtleAI deserialize() {
        Parser.consumeEndObjectProperty();

        return new TurtleAI();
    }
}
