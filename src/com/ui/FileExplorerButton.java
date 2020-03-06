package com.ui;

import com.component.Sprite;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.jade.GameObject;
import com.jade.Window;
import com.util.Constants;

import javax.swing.JFileChooser;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class FileExplorerButton extends JButton {
    private int labelId;
    private Label label;

    public FileExplorerButton(int labelId, Vector2 size) {
        super();
        this.labelId = labelId;
        this.size = size;
        this.position = new Vector2();
    }

    @Override
    public void start() {
        this.label = (Label)parent.getJComponent(labelId);
        Constants.CURRENT_LEVEL = this.label.text;
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
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        if (active) g2.setColor(Color.GRAY);
        g2.fillRect((int)this.position.x, (int)this.position.y, (int)this.size.x, (int)this.size.y);
        g2.setColor(Color.BLACK);
        g2.drawString("Browse", (int)(this.position.x), this.position.y + 10);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("FileExplorerButton", tabSize));

        // Label
        builder.append(addIntProperty("labelID", labelId, tabSize + 1, true, true));

        // Size
        builder.append(beginObjectProperty("Size", tabSize + 1));
        builder.append(size.serialize(tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // ID
        builder.append(addIntProperty("ID", id, tabSize + 1, true, false));

        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static FileExplorerButton deserialize() {
        int labelId = Parser.consumeIntProperty("labelID");
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2 size = Vector2.deserialize();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        int id = Parser.consumeIntProperty("ID");
        Parser.consumeEndObjectProperty();

        FileExplorerButton button = new FileExplorerButton(labelId, size);
        button.id = id;
        return button;
    }
}
