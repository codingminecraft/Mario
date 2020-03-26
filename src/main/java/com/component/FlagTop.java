package com.component;

import com.file.Parser;
import com.jade.Component;
import com.physics.Trigger;

public class FlagTop extends Component {
    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(PlayerController.class) != null) {
            trigger.gameObject.getComponent(PlayerController.class).win(true);
        }
    }

    @Override
    public Component copy() {
        return new FlagTop();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("FlagTop", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static FlagTop deserialize() {
        Parser.consumeEndObjectProperty();

        return new FlagTop();
    }
}
