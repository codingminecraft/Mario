package com.component;

import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.jade.Camera;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.renderer.Quad;
import com.renderer.RenderComponent;
import com.sun.javafx.geom.Vec4f;
import com.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class Grid extends Component {

    Camera camera;
    public int gridWidth, gridHeight;
    private int numXSquares = 34;
    private int numYSquares = 21;
    private Vector2 offset;

    List<RenderComponent> renderObjs;

    public Grid() {
        this.offset = new Vector2();
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
                GameObject square = new GameObject("Square", new Transform(new Vector2(x, y)), -10);
                square.transform.scale.x = gridWidth;
                square.transform.scale.y = gridHeight;
                float color = (i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0) ? 0.0f : 1.0f;
                Quad renderComp = new Quad(new Vec4f(color, color, color, 0.3f));
                renderComp.gameObject = square;
                Window.getScene().addRenderable(renderComp);
                this.renderObjs.add(renderComp);
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

                int x = (int)(i * this.gridWidth + this.offset.x);
                int y = (int)(j * this.gridHeight + this.offset.y);
                comp.gameObject.transform.position.x = x;
                comp.gameObject.transform.position.y = y;
                if (comp.getColor().x == 0.0f) {
                    comp.setColor(1.0f, 1.0f, 1.0f, 0.3f);
                } else {
                    comp.setColor(0.0f, 0.0f, 0.0f, 0.3f);
                }
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
