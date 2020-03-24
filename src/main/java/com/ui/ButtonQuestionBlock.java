package com.ui;

import com.component.Spritesheet;
import com.component.bricks.QuestionBlock;
import com.dataStructure.AssetPool;
import com.file.Parser;
import com.renderer.quads.Rectangle;
import com.ui.buttons.JButton;
import org.joml.Vector2f;

public class ButtonQuestionBlock extends JButton {
    private int type;
    private QuestionBlock currentBlock = null;

    public void setQuestionBlock(QuestionBlock block) {
        this.currentBlock = block;
    }

    public ButtonQuestionBlock(int type) {
        Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");

        this.type = type;
        switch (this.type) {
            case 0:
                this.mainComp = new Rectangle(items.sprites.get(7), new Vector2f(32, 32));
                break;
            case 1:
                this.mainComp = new Rectangle(items.sprites.get(10), new Vector2f(32, 32));
                break;
            case 2:
                this.mainComp = new Rectangle(items.sprites.get(24), new Vector2f(32, 32));
                break;
            default:
                assert false : "Unknown button question block type.";
        }
        this.renderComponents.add(this.mainComp);
    }

    @Override
    public void clicked() {
        System.out.println("Set type to: " + this.type);
        this.currentBlock.setType(this.type);
        this.currentBlock.deselectBlock();
    }

    @Override
    public String serialize(int tabSize) {
        return beginObjectProperty("ButtonQuestionBlock", tabSize) +
                addIntProperty("Type", this.type, tabSize + 1, true, false) +
                closeObjectProperty(tabSize);
    }

    public static ButtonQuestionBlock deserialize() {
        int type = Parser.consumeIntProperty("Type");

        Parser.consumeEndObjectProperty();

        return new ButtonQuestionBlock(type);
    }
}
