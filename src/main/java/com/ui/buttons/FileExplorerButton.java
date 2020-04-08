package com.ui.buttons;

import com.file.Parser;
import com.file.Serialize;
import com.jade.Window;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import com.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;

import java.io.File;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class FileExplorerButton extends JButton {
    private Label label, buttonText;
    private String filename;
    private String text = "Browse";

    public FileExplorerButton(Vector2f size, String filename) {
        super();
        this.mainComp = new Rectangle(Constants.BUTTON_COLOR, new Vector4f(10, 10, 10, 10), Constants.BUTTON_COLOR, 0.1f);
        this.mainComp.setSize(size);
        this.renderComponents.add(mainComp);

        this.buttonText = new Label(Constants.DEFAULT_FONT_TEXTURE, text, new Vector2f(0, 0));
        this.buttonText.setZIndex(3);
        this.renderComponents.addAll(this.buttonText.getRenderComponents());
        this.label = new Label(Constants.DEFAULT_FONT_TEXTURE, filename, new Vector2f(0, 0));
        Constants.CURRENT_LEVEL = filename;
        this.filename = filename;
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
        PointerBuffer outPath = memAllocPointer(1);
        try {
            boolean fileClicked = checkResult(
                    NFD_OpenDialog("level", null, outPath),
                    outPath
            );

            if (fileClicked) {
                String filePath = outPath.getStringUTF8();
                File file = new File(filePath);
                String filename = file.getName();
                filename = filename.replaceFirst("[.][^.]+$", "");

                this.filename = filename;
                Constants.CURRENT_LEVEL = filename;
                Window.getScene().importLevel(this.filename);
            }

        } finally {
            memFree(outPath);
        }
    }

    private static boolean checkResult(int result, PointerBuffer path) {
        switch (result) {
            case NFD_OKAY:
                nNFD_Free(path.get(0));
                return true;
            case NFD_CANCEL:
                return false;
            default: // NFD_ERROR
                System.err.format("Error: %s\n", NFD_GetError());
                return false;
        }
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(Serialize.beginObjectProperty("FileExplorerButton", tabSize));

        // Size
        builder.append(Serialize.beginObjectProperty("Size", tabSize + 1));
        builder.append(JMath.serialize(mainComp.getSize(), tabSize + 2));
        builder.append(Serialize.closeObjectProperty(tabSize + 1));
        builder.append(Serialize.addEnding(true, true));

        // Filename
        builder.append(Serialize.addStringProperty("Filename", filename, tabSize + 1, true, true));

        // ID
        builder.append(Serialize.addIntProperty("ID", id, tabSize + 1, true, false));

        builder.append(Serialize.closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static FileExplorerButton deserialize() {
        Parser.consumeBeginObjectProperty("Size");
        Vector2f size = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        String filename = Parser.consumeStringProperty("Filename");
        Parser.consume(',');

        int id = Parser.consumeIntProperty("ID");
        Parser.consumeEndObjectProperty();

        FileExplorerButton button = new FileExplorerButton(size, filename);
        button.id = id;
        return button;
    }
}
