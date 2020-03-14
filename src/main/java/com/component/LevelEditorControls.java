package com.component;

import com.dataStructure.Tuple;
import com.jade.*;
import com.util.Constants;
import org.joml.Vector2f;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorControls extends Component {
    private float debounceTime = 0.1f;
    private float debounceLeft = 0.0f;
    private float keyDebounceTime = 0.1f;
    private float keyDebounceLeft = 0.0f;
    private boolean placingBlocks = false;

    int gridWidth, gridHeight;
    Sprite sprite = null;
    AnimationMachine machine = null;

    private float screenX, screenY;
    private List<GameObject> selected;

    public LevelEditorControls(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.selected = new ArrayList<>();
    }

    public void gameObjectAdded() {
        placingBlocks = true;
        sprite = gameObject.getComponent(Sprite.class);
        machine = gameObject.getComponent(AnimationMachine.class);
    }

    public void gameObjectRemoved() {
        placingBlocks = false;
        sprite = null;
        machine = null;
    }

    private void calculateGameObjectPosition() {
        screenX = (float)Math.floor((MouseListener.getX() + Window.getScene().camera.position().x + MouseListener.getDx()) / gridWidth);
        screenY= (float)Math.floor((MouseListener.getY() + Window.getScene().camera.position().y + MouseListener.getDy()) / gridHeight);
        this.gameObject.transform.position.x = screenX * gridWidth - Window.getScene().camera.position().x;
        this.gameObject.transform.position.y = screenY * gridHeight - Window.getScene().camera.position().y;
    }

    private void placeGameObject() {
        Tuple<Integer> gridPos = new Tuple<>((int)(screenX * gridWidth), (int)(screenY * gridHeight), Constants.Z_INDEX);
        // Check if object has already been placed there
        // If not, we will place a block
        if (!Window.getScene().getWorldPartition().containsKey(gridPos) && !Window.getScene().inJWindow(MouseListener.position())) {
            GameObject object = gameObject.copy();
            object.transform.position = new Vector2f(screenX * gridWidth, screenY * gridHeight);
            object.zIndex = Constants.Z_INDEX;
            object.start();
            Window.getScene().addGameObject(object);
        }
    }

    private boolean leftMouseButtonClicked() {
        return MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1) &&
                debounceLeft < 0;
    }

    private boolean leftMouseButtonClickedNoDebounce() {
        return MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1);
    }

    private boolean shiftKey() {
        return KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
    }

    private boolean deleteKey() {
        return KeyListener.isKeyPressed(GLFW_KEY_DELETE);
    }

    private boolean arrowKeyPressed() {
        return (KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_DOWN) ||
                KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) && keyDebounceLeft < 0.0f;
    }

    private void selectGameObject() {
        Tuple<Integer> gridPos = new Tuple<>((int)(screenX * gridWidth), (int)(screenY * gridHeight), Constants.Z_INDEX);
        GameObject obj = Window.getScene().getWorldPartition().get(gridPos);
        if (obj != null) {
            if (!selected.contains(obj)) {
                obj.getComponent(Sprite.class).isSelected = true;
                selected.add(obj);
            } else {
                obj.getComponent(Sprite.class).isSelected = false;
                selected.remove(obj);
            }
        }
    }

    private void deselectAll() {
        for (GameObject go : selected) {
            go.getComponent(Sprite.class).isSelected = false;
        }
        selected.clear();
    }

    private void deleteSelected() {
        for (GameObject go : selected) {
            Window.getScene().deleteGameObject(go);
        }
        selected.clear();
    }

    private void moveSelectedObjects() {
        Vector2f direction = new Vector2f(0, 0);
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            direction.y = -1;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            direction.y = 1;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            direction.x = -1;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            direction.x = 1;
        }

        for (GameObject go : selected) {
            Window.getScene().moveGameObject(go, direction);
        }
    }

    @Override
    public void update(double dt) {
        debounceLeft -= dt;
        keyDebounceLeft -= dt;
        calculateGameObjectPosition();

        if (placingBlocks) {
            if (leftMouseButtonClickedNoDebounce()) {
                placeGameObject();
            }
        }

        if (!placingBlocks) {
            if (leftMouseButtonClicked()) {
                debounceLeft = debounceTime;
                if (shiftKey()) {
                    selectGameObject();
                } else {
                    deselectAll();
                    selectGameObject();
                }
            } else if (deleteKey()) {
                deleteSelected();
            } else if (arrowKeyPressed()) {
                moveSelectedObjects();
                keyDebounceLeft = keyDebounceTime;
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
