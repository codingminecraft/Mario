package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Collision;
import com.physics.Trigger;

public class Fireball extends Component {

    private boolean goingRight = true;
    private float fireballLife = 3.0f;

    public Fireball() {

    }

    @Override
    public void start() {
        if (gameObject.getComponent(Rigidbody.class).velocity.x < 0) {
            goingRight = false;
        }
    }

    @Override
    public void update(double dt) {
        fireballLife -= dt;
        if (fireballLife < 0) {
            Window.getScene().deleteGameObject(this.gameObject);
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == Collision.CollisionSide.BOTTOM) {
            this.gameObject.getComponent(Rigidbody.class).velocity.y = 400f;
        } else if (coll.side == Collision.CollisionSide.RIGHT && goingRight) {
            goingRight = false;
            this.gameObject.getComponent(Rigidbody.class).acceleration.x *= -1;
        } else if (coll.side == Collision.CollisionSide.LEFT && !goingRight) {
            goingRight = true;
            this.gameObject.getComponent(Rigidbody.class).acceleration.x *= -1;
        }
    }

    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(GoombaAI.class) != null) {
            trigger.gameObject.getComponent(GoombaAI.class).die(false);
            Window.getScene().deleteGameObject(this.gameObject);
        } else if (trigger.gameObject.getComponent(TurtleAI.class) != null) {
            trigger.gameObject.getComponent(TurtleAI.class).turnIntoShell();
            Window.getScene().deleteGameObject(this.gameObject);
        }
    }


    @Override
    public Component copy() {
        return new Fireball();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Fireball", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Fireball deserialize() {
        Parser.consumeEndObjectProperty();

        return new Fireball();
    }
}
