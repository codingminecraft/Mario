package com.component;

import com.dataStructure.Transform;
import com.jade.Camera;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.renderer.RenderComponent;
import com.util.Constants;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Grid extends Component {

    Camera camera;
    public int gridWidth, gridHeight;
    private int numXSquares = 34;
    private int numYSquares = 21;
    private Vector2f offset;

    List<RenderComponent> renderObjs;

    public Grid() {
        this.offset = new Vector2f();
        this.renderObjs = new ArrayList<>();
    }

    @Override
    public void start() {
        this.camera = Window.getScene().camera;
        this.gridHeight = Constants.TILE_HEIGHT;
        this.gridWidth = Constants.TILE_WIDTH;

        for (int i=0; i < numXSquares; i++) {
            for (int j=0; j < numYSquares; j++) {
                int x = (int)(i * this.gridWidth + this.offset.x);
                int y = (int)(j * this.gridHeight + this.offset.y);
                GameObject square = new GameObject("Square", new Transform(new Vector2f(x, y)), -10);
                square.transform.scale.x = gridWidth;
                square.transform.scale.y = gridHeight;

//                Rectangle rect = new Rectangle(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), new Vector4f(0.7f, 0.7f, 0.7f, 1.0f), 0.5f);
//                square.addRenderComponent(rect);
//                Window.getScene().addGameObject(square);
//
//                this.renderObjs.add(rect);
            }
        }

        calculateOffset();
    }

    private void calculateOffset() {
        int offsetX = (int)(Math.floor(camera.position().x / gridWidth) * gridWidth);
        int offsetY = (int)(Math.floor(camera.position().y / gridHeight) * gridHeight);

        if (this.offset.x != offsetX || this.offset.y != offsetY) {
            this.offset.x = offsetX;
            this.offset.y = offsetY;
            int i = 0;
            int j = 0;
            for (RenderComponent comp : renderObjs) {
                if (j >= numYSquares) {
                    j = 0;
                    i++;
                }

                comp.gameObject.transform.position.x = i * this.gridWidth + this.offset.x;
                comp.gameObject.transform.position.y = j * this.gridHeight + this.offset.y;
                comp.isDirty = true;

                j++;
            }
        }
    }

    @Override
    public void update(double dt) {
        calculateOffset();
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
