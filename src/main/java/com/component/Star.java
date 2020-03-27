package com.component;

import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Trigger;

public class Star extends Component {

    @Override
    public void update(double dt) {

    }

    @Override
    public void trigger(Trigger trigger) {
        if (trigger.gameObject.getComponent(PlayerController.class) != null) {
            trigger.gameObject.getComponent(PlayerController.class).setState(PlayerType.STAR);
            Window.getScene().deleteGameObject(this.gameObject);
        }
    }

    @Override
    public Component copy() {
        return new Star();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Star", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Star deserialize() {
        Parser.consumeEndObjectProperty();

        return new Star();
    }
}
