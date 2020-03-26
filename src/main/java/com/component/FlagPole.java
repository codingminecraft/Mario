package com.component;

import com.dataStructure.AssetPool;
import com.file.Parser;
import com.jade.Component;
import com.physics.Trigger;

public class FlagPole extends Component {

    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(PlayerController.class) != null) {
            trigger.gameObject.getComponent(PlayerController.class).win(false);
        }
    }

    @Override
    public Component copy() {
        return new FlagPole();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("FlagPole", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static FlagPole deserialize() {
        Parser.consumeEndObjectProperty();

        return new FlagPole();
    }
}
