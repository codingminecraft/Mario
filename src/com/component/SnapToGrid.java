package com.component;

import com.dataStructure.Transform;
import com.dataStructure.Tuple;
import com.dataStructure.Vector2;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.LevelEditorScene;
import com.jade.Window;
import com.ui.Button;
import com.util.Constants;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SnapToGrid extends Component {
    private float debounceTime = 0.05f;
    private float debounceLeft = 0.0f;
    private boolean drawGameObject = false;

    int gridWidth, gridHeight;
    Sprite sprite = null;
    AnimationMachine machine = null;

    public SnapToGrid(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public void gameObjectAdded() {
        drawGameObject = true;
        sprite = gameObject.getComponent(Sprite.class);
        machine = gameObject.getComponent(AnimationMachine.class);
    }

    public void gameObjectRemoved() {
        drawGameObject = false;
        sprite = null;
        machine = null;
    }

    @Override
    public void update(double dt) {
        debounceLeft -= dt;

        if (drawGameObject) {
            float x = (float)Math.floor((Window.getWindow().mouseListener.x + Window.getScene().camera.position.x + Window.getWindow().mouseListener.dx) / gridWidth);
            float y = (float)Math.floor((Window.getWindow().mouseListener.y + Window.getScene().camera.position.y + Window.getWindow().mouseListener.dy) / gridWidth);
            this.gameObject.transform.position.x = x * gridWidth - Window.getScene().camera.position.x;
            this.gameObject.transform.position.y = y * gridWidth - Window.getScene().camera.position.y;

            if (Window.getWindow().mouseListener.mousePressed &&
                Window.getWindow().mouseListener.mouseButton == MouseEvent.BUTTON1 &&
                Window.mouseListener().x > Constants.TILES_MAX_X &&
                debounceLeft < 0) {
                debounceLeft = debounceTime;
                Tuple<Integer> gridPos = new Tuple<>((int)(x * gridWidth), (int)(y * gridHeight), Constants.Z_INDEX);
                // Check if object has already been placed there
                // If not, we will place a block
                if (!Window.getScene().getWorldPartition().containsKey(gridPos) && !Window.getScene().inJWindow(Window.mouseListener().position)) {
                    GameObject object = gameObject.copy();
                    object.transform.position = new Vector2(x * gridWidth, y * gridHeight);
                    object.zIndex = Constants.Z_INDEX;
                    Window.getScene().addGameObject(object);
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (sprite != null) {
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.drawImage(sprite.image, (int)gameObject.transform.position.x, (int)gameObject.transform.position.y,
                    (int)sprite.width * 2, (int)sprite.height * 2, null);
            alpha = 1.0f;
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
        } else if (machine != null) {
            sprite = machine.getPreviewSprite();
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.drawImage(sprite.image, (int)gameObject.transform.position.x, (int)gameObject.transform.position.y,
                    (int)sprite.width * 2, (int)sprite.height * 2, null);
            alpha = 1.0f;
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
        }
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return "";
    }
}
