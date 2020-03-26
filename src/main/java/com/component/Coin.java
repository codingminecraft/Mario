package com.component;

import com.dataStructure.AssetPool;
import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Trigger;

public class Coin extends Component {
    private boolean doCollectAnimation = false;
    private float distanceY = 20;

    private float alphaSpeed = 10;
    private float speed = 120f;
    private float maxY;

    private SpriteRenderer renderer;
    private AnimationMachine machine;

    @Override
    public void start() {
        this.maxY = this.gameObject.transform.position.y + distanceY;
        this.renderer = gameObject.getComponent(SpriteRenderer.class);
        this.machine = gameObject.getComponent(AnimationMachine.class);
    }

    public void collect() {
        AssetPool.getSound("assets/sounds/coin.ogg").play();
        this.doCollectAnimation = true;
        this.machine.trigger("CollectCoin");
        System.out.println("Collect coin!");
    }

    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(PlayerController.class) != null) {
            collect();
        }
    }

    @Override
    public void update(double dt) {
        if (doCollectAnimation) {
            this.gameObject.transform.position.y += speed * dt;
            this.renderer.color.w -= alphaSpeed * dt;
            if (this.gameObject.transform.position.y > this.maxY) {
                Window.getScene().deleteGameObject(this.gameObject);
            }
        }
    }

    @Override
    public Component copy() {
        return new Coin();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Coin", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Coin deserialize() {
        Parser.consumeEndObjectProperty();

        return new Coin();
    }
}
