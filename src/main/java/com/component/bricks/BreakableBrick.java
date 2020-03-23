package com.component.bricks;

import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;

public class BreakableBrick extends Brick {
    @Override
    public void brickHit(GameObject player) {

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
