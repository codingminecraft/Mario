package com.component.bricks;

import com.component.Coin;
import com.component.GoombaAI;
import com.component.PlayerController;
import com.component.enums.PlayerType;
import com.dataStructure.Transform;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.physics.Collision;
import com.physics.Collision.CollisionSide;
import com.prefabs.Prefabs;
import org.joml.Vector2f;

public abstract class Brick extends Component {

    private boolean doAnimation = false;
    private boolean movingUp = true;
    protected float originalY, maxY;

    protected float yDistance = 8;
    private boolean canDoAnimation = true;
    private boolean animationPlayedOnce = false;

    public void setInactive() {
        this.canDoAnimation = false;
    }

    public abstract void brickHit(GameObject player);

    protected void spawnCoin() {
        GameObject coin = Prefabs.COIN();
        coin.transform.position.x = this.gameObject.transform.position.x;
        coin.transform.position.y = this.gameObject.transform.position.y + this.gameObject.transform.scale.y;
        Coin coinComp = coin.getComponent(Coin.class);
        Window.getScene().addGameObject(coin);
        coin.start();
        coinComp.collect();
    }

    protected void spawnPowerup(PlayerType type) {
        GameObject powerup = null;
        if (type == PlayerType.SMALL) {
            powerup = Prefabs.MUSHROOM_ITEM();
        } else if (type == PlayerType.BIG) {
            powerup = Prefabs.FLOWER_ITEM();
        } else if (type == PlayerType.FIRE) {
            powerup = Prefabs.COIN();
        }
        powerup.transform.position.x = this.gameObject.transform.position.x;
        powerup.transform.position.y = this.gameObject.transform.position.y + this.gameObject.transform.scale.y;
        Window.getScene().addGameObject(powerup);
    }

    protected void spawnStar() {

    }

    @Override
    public void start() {
        this.originalY = this.gameObject.transform.position.y;
        this.maxY = this.originalY + yDistance;
    }

    @Override
    public void update(double dt) {
        if (!canDoAnimation && animationPlayedOnce) return;

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
                    animationPlayedOnce = true;
                    this.gameObject.transform.position.y = originalY;
                }
            }
        }
    }

    @Override
    public void collision(Collision coll) {
        if (coll.side == CollisionSide.BOTTOM &&
                coll.contactPoint.x > this.gameObject.transform.position.x + 1 &&
                coll.contactPoint.x < this.gameObject.transform.position.x + coll.bounds.getWidth() - 1 &&
                coll.gameObject.getComponent(PlayerController.class) != null && canDoAnimation) {
            doAnimation = true;
            brickHit(coll.gameObject);
        }

        if (coll.side == CollisionSide.TOP && doAnimation && movingUp &&coll.gameObject.getComponent(GoombaAI.class) != null) {
            coll.gameObject.getComponent(GoombaAI.class).die(false);
        }
    }
}
