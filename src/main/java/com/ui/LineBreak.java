package com.ui;

import com.dataStructure.Vector2;
import com.file.Parser;

public class LineBreak extends JComponent {

    public LineBreak() {
        this.position = new Vector2();
        this.size = new Vector2();
        this.isLineBreak = true;
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("LineBreak", tabSize));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static LineBreak deserialize() {
        Parser.consumeEndObjectProperty();

        return new LineBreak();
    }
}
