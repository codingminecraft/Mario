package com.ui;

import com.component.LevelEditorControls;
import com.component.Sprite;
import com.dataStructure.Transform;
import com.file.Parser;
import com.jade.*;
import com.util.JMath;
import org.joml.Vector2f;

import java.awt.Graphics2D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class Button extends JComponent {
    public static int ACTIVE_ITEM = -1;

    private boolean hot = false;
    private boolean active = false;
    private Sprite sprite;
    private GameObject objToCopy;

    public Button(Sprite sprite, Vector2f position, Vector2f size, GameObject objToCopy) {
        super();
        this.sprite = sprite;
        this.renderComponent.setPosition(position);
        this.renderComponent.setSize(size);
        this.objToCopy = objToCopy;
    }

    @Override
    public void update(double dt) {
        this.hot = mouseInButton();

        if (!active && hot && visible) {
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                active = true;
                ACTIVE_ITEM = this.id;
                LevelEditorScene scene = (LevelEditorScene) Window.getScene();
                LevelEditorControls levelEditorControls = scene.mouseCursor.getComponent(LevelEditorControls.class);
                scene.mouseCursor = objToCopy.copy();
                scene.mouseCursor.addComponent(levelEditorControls);
                scene.mouseCursor.start();
                levelEditorControls.gameObjectAdded();
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            active = false;
            ACTIVE_ITEM = -1;
        } else if (active && ACTIVE_ITEM != this.id) {
            active = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
//        if (active) {
//            float alpha = 0.5f;
//            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//            g2.setComposite(ac);
//            g2.drawImage(sprite.image, (int)position.x, (int)position.y, (int)size.x, (int)size.y, null);
//            alpha = 1.0f;
//            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//            g2.setComposite(ac);
//        } else {
//            g2.drawImage(sprite.image, (int)position.x, (int)position.y, (int)size.x, (int)size.y, null);
//        }
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
        builder.append(JMath.serialize(renderComponent.getPosition(), tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // Size
        builder.append(beginObjectProperty("Size", tabSize + 1));
        builder.append(JMath.serialize(renderComponent.getSize(), tabSize + 2));
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
        Vector2f position = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2f size = JMath.deserializeVector2f();
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
