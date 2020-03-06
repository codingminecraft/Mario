package com.ui;

import com.component.SnapToGrid;
import com.component.Sprite;
import com.dataStructure.AssetPool;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.LevelEditorScene;
import com.jade.Window;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Button extends JComponent {
    public static int ACTIVE_ITEM = -1;

    private boolean hot = false;
    private boolean active = false;
    private Sprite sprite;
    private GameObject objToCopy;

    public Button(Sprite sprite, Vector2 position, Vector2 size, GameObject objToCopy) {
        super();
        this.sprite = sprite;
        this.position = position;
        this.size = size;
        this.objToCopy = objToCopy;
    }

    @Override
    public void update(double dt) {
        this.hot = Window.mouseListener().x > this.position.x && Window.mouseListener().x < this.position.x + this.size.x &&
                Window.mouseListener().y > this.position.y && Window.mouseListener().y < this.position.y + this.size.y;

        if (!active && hot) {
            if (Window.mouseListener().mousePressed && Window.mouseListener().mouseButton == MouseEvent.BUTTON1) {
                active = true;
                ACTIVE_ITEM = this.id;
                LevelEditorScene scene = (LevelEditorScene) Window.getScene();
                SnapToGrid snapToGrid = scene.mouseCursor.getComponent(SnapToGrid.class);
                scene.mouseCursor = objToCopy.copy();
                scene.mouseCursor.addComponent(snapToGrid);
            }
        } else if (Window.keyListener().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            active = false;
            ACTIVE_ITEM = -1;
        } else if (active && ACTIVE_ITEM != this.id) {
            active = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (active) {
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.drawImage(sprite.image, (int)position.x, (int)position.y, (int)size.x, (int)size.y, null);
            alpha = 1.0f;
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
        } else {
            g2.drawImage(sprite.image, (int)position.x, (int)position.y, (int)size.x, (int)size.y, null);
        }
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Button", tabSize));

        // Sprite
        builder.append(sprite.serialize(tabSize + 1));
        builder.append(addEnding(true, true));

        // Position
        builder.append(beginObjectProperty("Position", tabSize + 1));
        builder.append(position.serialize(tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // Size
        builder.append(beginObjectProperty("Size", tabSize + 1));
        builder.append(size.serialize(tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // ID
        builder.append(addIntProperty("ID", id, tabSize + 1, true, true));

        // ObjectToCopy
        builder.append(objToCopy.serialize(tabSize + 1));
        builder.append(addEnding(true, false));

        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static Button deserialize() {
        Sprite sprite = (Sprite)Parser.parseComponent();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Position");
        Vector2 position = Vector2.deserialize();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2 size = Vector2.deserialize();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        int id = Parser.consumeIntProperty("ID");
        Parser.consume(',');

        GameObject objToCopy = Parser.parseGameObject();
        Parser.consumeEndObjectProperty();

        Button button = new Button(sprite, position, size, objToCopy);
        button.id = id;
        return button;
    }
}
