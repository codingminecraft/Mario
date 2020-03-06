package com.ui;

import com.dataStructure.Vector2;
import com.file.Parser;
import com.jade.Scene;
import com.jade.Window;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;

public class SaveLevelButton extends JButton {
    String text = "Save Level";

    public SaveLevelButton() {
        this.position = new Vector2();
        this.size = new Vector2(70, 14);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("SaveLevelButton", tabSize));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static SaveLevelButton deserialize() {
        Parser.consumeEndObjectProperty();

        return new SaveLevelButton();
    }

    @Override
    public void clicked() {
        Scene scene = Window.getScene();
        scene.export(Constants.CURRENT_LEVEL);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        if (active) g2.setColor(Color.GRAY);
        g2.fillRect((int)this.position.x, (int)this.position.y, (int)this.size.x, (int)this.size.y);
        g2.setColor(Color.BLACK);
        g2.drawString(text, (int)(this.position.x), this.position.y + 10);
    }
}
