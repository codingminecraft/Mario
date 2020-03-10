package com.ui;

import com.dataStructure.Vector2;
import com.file.Parser;
import com.jade.Window;
import com.util.Constants;

import javax.swing.JFileChooser;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class FileExplorerButton extends JButton {
    private int labelId;
    private Label label;
    private Vector2 stringPos = new Vector2();
    private String text = "Browse";

    public FileExplorerButton(int labelId, Vector2 size) {
        super();
        this.labelId = labelId;
        this.size = size;
        this.position = new Vector2();
        //FontMetrics metrics = Constants.FONT_METRICS;
        //stringPos.x = (this.size.x / 2.0f) - (metrics.stringWidth(text) / 2.0f);
        //stringPos.y = (this.size.y / 2.0f) - (metrics.getHeight() / 2.0f) + 12;
    }

    @Override
    public void start() {
        this.label = (Label)parent.getJComponent(labelId);
        Constants.CURRENT_LEVEL = this.label.text;
        Window.getScene().importLevel(label.text);
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
        Window.getScene().importLevel(filename);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Constants.BUTTON_COLOR);
        if (active) g2.setColor(Color.GRAY);
        g2.fill(new RoundRectangle2D.Float(this.position.x, this.position.y, this.size.x, this.size.y, 15f, 13f));
        g2.setColor(Color.WHITE);
        g2.drawString(text, (int)(this.position.x + stringPos.x), this.position.y + stringPos.y);
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