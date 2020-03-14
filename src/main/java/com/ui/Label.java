package com.ui;

import com.file.Parser;
import org.joml.Vector2f;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Label extends JComponent {
    String text;
    FontMetrics fontMetrics;
    Vector2f stringPos = new Vector2f();

    public Label(String text) {
        super();

        this.text = text;
        //this.fontMetrics = Constants.FONT_METRICS;
        //this.size.x = this.fontMetrics.stringWidth(text) * 1.5f;
        //this.size.y = this.fontMetrics.getHeight();
        //stringPos.x = (this.size.x / 2.0f) - (fontMetrics.stringWidth(text) / 2.0f);
        //stringPos.y = (this.size.y / 2.0f) - (fontMetrics.getHeight() / 2.0f) + 12;
    }

    public void setText(String text) {
        this.text = text;
//        this.transform.scale.x = this.fontMetrics.stringWidth(text) * 1.5f;
//        this.transform.scale.y = this.fontMetrics.getHeight();
    }

    @Override
    public void draw(Graphics2D g2) {
//        g2.setColor(Color.WHITE);
//        g2.drawString(text, (int)(position.x + stringPos.x), (int)(position.y + stringPos.y));
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("Label", tabSize));

        // Text
        builder.append(addStringProperty("Text", this.text.replace("\\", "/"), tabSize + 1, true, true));

        // ID and center
        builder.append(addBooleanProperty("IsCentered", isCentered, tabSize + 1, true, true));
        builder.append(addIntProperty("ID", id, tabSize + 1, true, false));

        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static Label deserialize() {
        String text = Parser.consumeStringProperty("Text");
        Parser.consume(',');

        boolean isCentered = Parser.consumeBooleanProperty("IsCentered");
        Parser.consume(',');
        int id = Parser.consumeIntProperty("ID");
        Parser.consumeEndObjectProperty();

        Label label = new Label(text);
        label.id = id;
        label.isCentered = isCentered;
        return label;
    }
}
