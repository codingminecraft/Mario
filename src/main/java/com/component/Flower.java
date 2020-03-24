package com.component;

import com.component.enums.PlayerType;
import com.file.Parser;
import com.jade.Component;
import com.jade.Window;
import com.physics.Collision;
import com.physics.Trigger;

public class Flower extends Component {
    private boolean collected = false;

    @Override
    public void trigger(Trigger trigger) {
        PlayerController player = trigger.gameObject.getComponent(PlayerController.class);
        if (player != null && !collected) {
            player.setState(PlayerType.FIRE);
            Window.getScene().deleteGameObject(this.gameObject);
            this.collected = true;
        }
    }

    @Override
    public Component copy() {
        return new Flower();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("Flower", tabSize) +
                closeObjectProperty(tabSize);
    }

    public static Flower deserialize() {
        Parser.consumeEndObjectProperty();

        return new Flower();
    }
}
