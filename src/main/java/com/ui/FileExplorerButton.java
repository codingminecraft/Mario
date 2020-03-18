package com.ui;

import com.file.Parser;
import com.jade.Window;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import com.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

import javax.swing.JFileChooser;
import java.awt.Graphics2D;
import java.io.File;

public class FileExplorerButton extends JButton {
    private Label label, buttonText;
    private String text = "Browse";

    public FileExplorerButton(Vector2f size) {
        super();
        this.mainComp = new Rectangle(Constants.BUTTON_COLOR, new Vector4f(10, 10, 10, 10), Constants.BUTTON_COLOR, 0.1f);
        this.mainComp.setSize(size);
        this.renderComponents.add(mainComp);

        this.buttonText = new Label(Constants.DEFAULT_FONT_TEXTURE, text, new Vector2f(0, 0));
        this.buttonText.setZIndex(3);
        this.renderComponents.addAll(this.buttonText.getRenderComponents());
        this.label = new Label(Constants.DEFAULT_FONT_TEXTURE, Constants.CURRENT_LEVEL, new Vector2f(0, 0));
        this.label.setZIndex(3);
        this.renderComponents.addAll(this.label.getRenderComponents());
    }

    @Override
    public void setPosX(float x) {
        this.label.setPosX(x);
        x += this.label.getWidth() + Constants.PADDING.x;
        this.mainComp.setPosX(x);

        this.buttonText.setPosX(x + (this.mainComp.getWidth() / 2.0f) - (this.label.getWidth() / 2.0f));
    }

    @Override
    public void setPosY(float y) {
        this.label.setPosY(y + (this.mainComp.getHeight() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getLineHeight() / 2.0f) + 2);
        this.mainComp.setPosY(y);

        this.buttonText.setPosY(y + (this.mainComp.getHeight() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getLineHeight() / 2.0f) + 2);
    }

    @Override
    public float getWidth() {
        return this.label.getWidth() + this.mainComp.getWidth();
    }

    @Override
    public void start() {
        Constants.CURRENT_LEVEL = this.label.getText();
        Window.getScene().importLevel(label.getText());
    }

    @Override
    public void clicked() {
        JFileChooser f = new JFileChooser(new File("./levels"));
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        f.showSaveDialog(null);

        File selectedFile = f.getSelectedFile();
        String filename = "Default";
        if (selectedFile != null) {
            filename = selectedFile.getName();
            filename = filename.replaceFirst("[.][^.]+$", "");
        }

        this.label.setText(filename);
        Constants.CURRENT_LEVEL = filename;
        //Window.getScene().importLevel(filename);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("FileExplorerButton", tabSize));

        // Position
        builder.append(beginObjectProperty("Position", tabSize + 1));
        builder.append(JMath.serialize(mainComp.getPosition(), tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // Size
        builder.append(beginObjectProperty("Size", tabSize + 1));
        builder.append(JMath.serialize(mainComp.getSize(), tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // ID
        builder.append(addIntProperty("ID", id, tabSize + 1, true, false));

        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static FileExplorerButton deserialize() {
        Parser.consumeBeginObjectProperty("Position");
        Vector2f position = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2f size = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        int id = Parser.consumeIntProperty("ID");
        Parser.consumeEndObjectProperty();

        FileExplorerButton button = new FileExplorerButton(size);
        button.id = id;
        return button;
    }
}
