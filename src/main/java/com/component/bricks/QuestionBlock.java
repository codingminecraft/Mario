package com.component.bricks;

import com.component.AnimationMachine;
import com.component.PlayerController;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.ui.ButtonQuestionBlock;
import com.ui.JComponent;
import com.ui.JWindow;
import org.joml.Vector2f;

public class QuestionBlock extends Brick {
    private AnimationMachine machine = null;
    // Types
    // 0      1                2
    // Coin   Mushroom/Flower  Star
    private int type = 0;
    private String windowTitle;
    private JWindow window;

    public QuestionBlock(String windowTitle) {
        this.windowTitle = windowTitle;
        this.type = 0;
    }

    public QuestionBlock(String windowTitle, int type) {
        this.windowTitle = windowTitle;
        this.type = type;
    }

    @Override
    public void start() {
        machine = gameObject.getComponent(AnimationMachine.class);
        this.originalY = this.gameObject.transform.position.y;
        this.maxY = this.originalY + yDistance;
        this.window = Window.getScene().getJWindow(this.windowTitle);
    }

    public void blockSelected() {
        Vector2f newPos = new Vector2f(this.gameObject.transform.position.x, this.gameObject.transform.position.y + this.gameObject.transform.scale.y);
        this.window.setPosition(Window.getScene().camera.worldToScreen(newPos));
        for (JComponent comp : this.window.getAllComponents()) {
            if (comp instanceof ButtonQuestionBlock) {
                ButtonQuestionBlock button = (ButtonQuestionBlock)comp;
                button.setQuestionBlock(this);
            }
        }
    }

    public void deselectBlock() {
        this.window.setPosition(new Vector2f(-1000, 0));
    }

    public void setType(int type) {
        this.type = type;
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
        return new QuestionBlock(windowTitle, this.type);
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("QuestionBlock", tabSize) +
                addIntProperty("Type", this.type, tabSize + 1, true, true) +
                addStringProperty("WindowTitle", this.windowTitle, tabSize + 1, true, false) +
                closeObjectProperty(tabSize);
    }

    public static QuestionBlock deserialize() {
        int type = Parser.consumeIntProperty("Type");
        Parser.consume(',');

        String windowTitle = Parser.consumeStringProperty("WindowTitle");

        Parser.consumeEndObjectProperty();

        return new QuestionBlock(windowTitle, type);
    }
}
