package com.ui;

import com.dataStructure.Vector2;
import com.file.Parser;
import com.jade.Scene;
import com.jade.Window;
import com.util.Constants;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public class SaveLevelButton extends JButton {
    String text = "Save Level";
    Vector2 stringPos = new Vector2();

    public SaveLevelButton() {
        super();
        this.position = new Vector2();
        this.size = new Vector2(86, 20);
        //FontMetrics metrics = Constants.FONT_METRICS;
        //stringPos.x = (this.size.x / 2.0f) - (metrics.stringWidth(text) / 2.0f);
        //stringPos.y = (this.size.y / 2.0f) - (metrics.getHeight() / 2.0f) + 12;
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
        g2.setColor(Constants.BUTTON_COLOR);
        if (active) g2.setColor(Color.GRAY);
        g2.fill(new RoundRectangle2D.Float(this.position.x, this.position.y, this.size.x, this.size.y, 15f, 13f));
        g2.setColor(Color.WHITE);
        g2.drawString(text, (int)(this.position.x + stringPos.x), this.position.y + stringPos.y);
    }
}
