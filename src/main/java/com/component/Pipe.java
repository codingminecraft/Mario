package com.component;

import com.file.Parser;
import com.jade.*;
import com.physics.Collision;
import com.ui.JWindow;
import com.util.Constants;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {
    private static int NUM_PIPES = 0;
    private static int SELECTED_ENTRANCE = -1;
    private int id;
    private boolean isEntrance;
    // 0 = down, 1 = up, 2 = right, 3 = left
    private int type;

    private boolean isSelected = false;

    private int otherPipeId = -1;
    private JWindow pipeSelectionWindow;
    private GameObject otherPipe = null;

    public Pipe(boolean isEntrance, int type) {
        this.id = Pipe.NUM_PIPES;
        Pipe.NUM_PIPES++;
        this.isEntrance = isEntrance;
        this.type = type;
    }

    public Pipe(boolean isEntrance, int type, int id) {
        this.id = id;
        this.isEntrance = isEntrance;
        this.type = type;
        if (this.id > NUM_PIPES) {
            NUM_PIPES = this.id + 1;
        }
    }

    public Pipe(boolean isEntrance, int type, int id, int otherPipeId) {
        this.isEntrance = isEntrance;
        this.id = id;
        this.otherPipeId = otherPipeId;
        this.type = type;
        if (this.id > NUM_PIPES) {
            NUM_PIPES = this.id + 1;
        }
    }

    @Override
    public void start() {
        if (Window.getScene() instanceof LevelEditorScene) {
            updateColor();
            this.pipeSelectionWindow = Window.getScene().getJWindow("Exit Pipe Selection");
        }

        if (otherPipeId == -1 && !isEntrance) {
            for (GameObject go : Window.getScene().getGameObjectsWithComponent(Pipe.class)) {
                if (go.getComponent(Pipe.class).getID() == Pipe.SELECTED_ENTRANCE) {
                    this.otherPipeId = go.getComponent(Pipe.class).getID();
                    go.getComponent(Pipe.class).setOtherPipe(this.id);
                    break;
                }
            }
        } else if (otherPipe == null) {
            findOtherPipe();
        }
    }

    private void findOtherPipe() {
        for (GameObject go : Window.getScene().getGameObjectsWithComponent(Pipe.class)) {
            if (go.getComponent(Pipe.class).getID() == otherPipeId) {
                this.otherPipe = go;
                break;
            }
        }
    }

    private void updateColor() {
        if (isEntrance) {
            gameObject.getComponent(SpriteRenderer.class).color = Constants.COLOR_PURPLE;
        }
        else {
            gameObject.getComponent(SpriteRenderer.class).color = Constants.COLOR_ORANGE;
        }
    }

    @Override
    public void collision(Collision coll) {
        PlayerController playerController = coll.gameObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (otherPipe == null) findOtherPipe();
            if (coll.side == Collision.CollisionSide.TOP && this.type == 1 && (KeyListener.isKeyPressed(GLFW_KEY_DOWN) || KeyListener.isKeyPressed(GLFW_KEY_S))) {
                playerController.gameObject.transform.position.x = otherPipe.transform.position.x;
                playerController.gameObject.transform.position.y = otherPipe.transform.position.y;
            } else if (coll.side == Collision.CollisionSide.BOTTOM && this.type == 0 && (KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W))) {
                playerController.gameObject.transform.position.x = otherPipe.transform.position.x;
                playerController.gameObject.transform.position.y = otherPipe.transform.position.y + otherPipe.transform.scale.y;
            } else if (coll.side == Collision.CollisionSide.RIGHT && this.type == 2 && (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A))) {
                playerController.gameObject.transform.position.x = otherPipe.transform.position.x;
                playerController.gameObject.transform.position.y = otherPipe.transform.position.y;
            } else if (coll.side == Collision.CollisionSide.LEFT && this.type == 3 && (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D))) {
                playerController.gameObject.transform.position.x = otherPipe.transform.position.x;
                playerController.gameObject.transform.position.y = otherPipe.transform.position.y;
            }
        }
    }

    public boolean isEntrance() {
        return this.isEntrance;
    }

    public void setOtherPipe(int id) {
        this.otherPipeId = id;
    }

    public void blockSelected() {
        this.isSelected = true;
        if (this.otherPipeId == -1 && isEntrance) {
            Vector2f newPos = new Vector2f(this.gameObject.transform.position.x, this.gameObject.transform.position.y + this.gameObject.transform.scale.y);
            this.pipeSelectionWindow.setPosition(Window.getScene().camera.worldToScreen(newPos));
            Pipe.SELECTED_ENTRANCE = this.id;
        }
    }

    public void blockDeselected() {
        this.isSelected = false;
        updateColor();
        this.pipeSelectionWindow.setPosition(new Vector2f(-1000, 0));
    }

    public int getID() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public Component copy() {
        return new Pipe(isEntrance, this.type, this.id);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Pipe", tabSize));

        builder.append(addIntProperty("ID", this.id, tabSize + 1, true, true));
        builder.append(addIntProperty("OtherPipeID", this.otherPipeId, tabSize + 1, true, true));
        builder.append(addIntProperty("Type", this.type, tabSize + 1, true, true));
        builder.append(addBooleanProperty("IsEntrance", this.isEntrance, tabSize + 1, true, false));

        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Pipe deserialize() {
        int id = Parser.consumeIntProperty("ID");
        Parser.consume(',');

        int otherPipeId = Parser.consumeIntProperty("OtherPipeID");
        Parser.consume(',');

        int type = Parser.consumeIntProperty("Type");
        Parser.consume(',');

        boolean isEntrance = Parser.consumeBooleanProperty("IsEntrance");
        Parser.consumeEndObjectProperty();

        return new Pipe(isEntrance, type, id, otherPipeId);
    }
}
