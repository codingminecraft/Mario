package com.component.bricks;

import com.component.AnimationMachine;
import com.component.PlayerController;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;

public class QuestionBlock extends Brick {
    private AnimationMachine machine = null;
    // Types
    // 0      1                2
    // Coin   Mushroom/Flower  Star
    private int type = 0;

    @Override
    public void start() {
        machine = gameObject.getComponent(AnimationMachine.class);
        this.originalY = this.gameObject.transform.position.y;
        this.maxY = this.originalY + yDistance;
    }

    @Override
    public void brickHit(GameObject player) {
        machine.trigger("HitBlock");
        setInactive();
        PlayerController controller = player.getComponent(PlayerController.class);

        switch(type) {
            case 0:
                spawnCoin();
                break;
            case 1:
                spawnPowerup(controller.type);
                break;
            case 2:
                spawnStar();
                break;
            default:
                assert false : "Unknown brick type '" + type + "' in QuestionBlock";
                break;
        }
    }

    @Override
    public Component copy() {
        return new QuestionBlock();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("QuestionBlock", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static QuestionBlock deserialize() {
        Parser.consumeEndObjectProperty();

        return new QuestionBlock();
    }
}
