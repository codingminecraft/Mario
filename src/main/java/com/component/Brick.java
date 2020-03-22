package com.component;

import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.physics.Collision;
import com.physics.Collision.CollisionSide;

public class Brick extends Component {

    private boolean doAnimation = false;
    private boolean movingUp = true;
    private float originalY, maxY;

    private float yDistance = 8;
    private boolean canDoAnimation = true;

    public void setInactive() {
        this.canDoAnimation = false;
    }

    @Override
    public void start() {
        this.originalY = this.gameObject.transform.position.y;
        this.maxY = this.originalY + yDistance;
    }

    @Override
    public void update(double dt) {
        if (!canDoAnimation) return;

        if (doAnimation) {
            if (movingUp) {
                this.gameObject.transform.position.y += 80 * dt;
                if (this.gameObject.transform.position.y > maxY) {
                    movingUp = false;
                }
            } else {
                this.gameObject.transform.position.y -= 80 * dt;
                if (this.gameObject.transform.position.y < originalY) {
                    movingUp = true;
                    doAnimation = false;
                    this.gameObject.transform.position.y = originalY;
                }
            }
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == CollisionSide.BOTTOM &&
                coll.contactPoint.x > this.gameObject.transform.position.x + 2 &&
                coll.contactPoint.x < this.gameObject.transform.position.x + coll.bounds.getWidth() - 2) {
            doAnimation = true;
        }
    }

    @Override
    public Component copy() {
        return new Brick();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Brick", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Brick deserialize() {
        Parser.consumeEndObjectProperty();

        return new Brick();
    }
}
