package com.component.bricks;

import com.component.PlayerController;
import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;

public class BreakableBrick extends Brick {
    @Override
    public void brickHit(GameObject player) {
        PlayerController playerController = player.getComponent(PlayerController.class);
        if (playerController.type == PlayerType.BIG || playerController.type == PlayerType.FIRE) {
            Window.getScene().deleteGameObject(this.gameObject);
        }
    }

    @Override
    public Component copy() {
        return new BreakableBrick();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("BreakableBrick", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static BreakableBrick deserialize() {
        Parser.consumeEndObjectProperty();

        return new BreakableBrick();
    }
}
