package com.ui;

import com.dataStructure.Transform;
import com.file.Parser;
import org.joml.Vector2f;

public class LineBreak extends JComponent {

    public LineBreak() {
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
