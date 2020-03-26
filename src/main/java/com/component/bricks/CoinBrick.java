package com.component.bricks;

import com.dataStructure.AssetPool;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;

public class CoinBrick extends Brick {

    @Override
    public void brickHit(GameObject player) {

    }

    @Override
    public Component copy() {
        return new CoinBrick();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("CoinBrick", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static CoinBrick deserialize() {
        Parser.consumeEndObjectProperty();

        return new CoinBrick();
    }
}
