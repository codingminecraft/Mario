package com.ui;

import com.component.Sprite;
import com.file.Parser;
import com.util.Constants;
import org.joml.Vector2f;

import java.awt.Graphics2D;

public class ZIndexButton extends JButton {
    int labelId;
    int direction;
//    Label label;
    Sprite sprite;

    public ZIndexButton(int direction, int labelId, Sprite sprite) {
        super();
        this.mainComp.setSize(new Vector2f(25, 22));
        this.direction = direction;
        this.labelId = labelId;
        this.sprite = sprite;
        this.isCentered = true;
        this.renderComponents.add(mainComp);
    }

    @Override
    public void start() {
//        this.label = (Label)parent.getJComponent(labelId);
    }

    @Override
    public void clicked() {
//        Constants.Z_INDEX += direction;
//        this.label.setText("" + Constants.Z_INDEX);
    }

    @Override
    public void draw(Graphics2D g2) {
//        g2.setColor(Color.WHITE);
//        if (active) g2.setColor(Color.GRAY);
//        g2.drawImage(sprite.image, (int)position.x, (int)position.y, (int)size.x, (int)size.y, null);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("ZIndexButton", tabSize));
        builder.append(addIntProperty("Direction", this.direction, tabSize + 1, true, true));
        builder.append(addIntProperty("LabelID", this.labelId, tabSize + 1, true, true));
        builder.append(sprite.serialize(tabSize + 2));
        builder.append(addEnding(true, true));

        // ID
        builder.append(addIntProperty("ID", id, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static ZIndexButton deserialize() {
        int direction = Parser.consumeIntProperty("Direction");
        Parser.consume(',');
        int labelId = Parser.consumeIntProperty("LabelID");
        Parser.consume(',');
        Sprite sprite = (Sprite)Parser.parseComponent().copy();
        Parser.consume(',');

        int id = Parser.consumeIntProperty("ID");
        Parser.consumeEndObjectProperty();

        ZIndexButton button = new ZIndexButton(direction, labelId, sprite);
        button.id = id;
        return button;
    }
}
