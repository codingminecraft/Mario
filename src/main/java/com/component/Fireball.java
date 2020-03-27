package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Collision;

public class Fireball extends Component {

    private float fireballLife = 3.0f;

    public Fireball() {

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
        if (coll.gameObject.getComponent(GoombaAI.class) != null) {
            coll.gameObject.getComponent(GoombaAI.class).die(false);
            Window.getScene().deleteGameObject(this.gameObject);
        } else if (coll.gameObject.getComponent(TurtleAI.class) != null) {
            coll.gameObject.getComponent(TurtleAI.class).turnIntoShell();
            Window.getScene().deleteGameObject(this.gameObject);
        } else if (coll.side == Collision.CollisionSide.BOTTOM) {
            this.gameObject.getComponent(Rigidbody.class).velocity.y = 200f;
        } else if (coll.side == Collision.CollisionSide.RIGHT || coll.side == Collision.CollisionSide.LEFT) {
            this.gameObject.getComponent(Rigidbody.class).acceleration.x *= -1;
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
