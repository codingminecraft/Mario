package com.component;

import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Collision;
import com.physics.Trigger;

public class Mushroom extends Component {
    private boolean goingRight = true;
    private float speed = 40f;
    private boolean collected = false;

    @Override
    public void update(double dt) {
        if (goingRight) {
            gameObject.transform.position.x += speed * dt;
        } else {
            gameObject.transform.position.x -= speed * dt;
        }
    }

    @Override
    public void trigger(Trigger trigger) {
        PlayerController player = trigger.gameObject.getComponent(PlayerController.class);
        if (player != null && !collected) {
            player.setState(PlayerType.BIG);
            Window.getScene().deleteGameObject(this.gameObject);
            this.collected = true;
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == Collision.CollisionSide.RIGHT) {
            goingRight = false;
        } else if (coll.side == Collision.CollisionSide.LEFT) {
            goingRight = true;
        }
    }

    @Override
    public Component copy() {
        return new Mushroom();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Mushroom", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Mushroom deserialize() {
        Parser.consumeEndObjectProperty();

        return new Mushroom();
    }
}
