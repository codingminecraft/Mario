package com.component;

import com.dataStructure.Transform;
import com.jade.Camera;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.renderer.RenderComponent;
import com.renderer.UIRenderComponent;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Grid extends Component {

    Camera camera;
    public int gridWidth, gridHeight;
    private int numXSquares = 41;
    private int numYSquares = 23;
    private Vector2f offset;

    List<UIRenderComponent> renderObjs;

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

                Rectangle rect = new Rectangle(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), new Vector4f(0.7f, 0.7f, 0.7f, 1.0f), 0.5f);
                rect.setPosX(x);
                rect.setPosY(y);
                rect.setWidth(this.gridWidth);
                rect.setHeight(this.gridHeight);
                Window.getScene().addLowUI(rect);

                this.renderObjs.add(rect);
            }
        }

        calculateOffset();
    }

    private void calculateOffset() {
        float offsetX = (float)(Math.floor(camera.position().x / gridWidth) * gridWidth) - camera.position().x;
        float offsetY = (float)(Math.floor(camera.position().y / gridHeight) * gridHeight) - camera.position().y;

        if (this.offset.x != offsetX || this.offset.y != offsetY) {
            this.offset.x = offsetX;
            this.offset.y = offsetY;
            int i = 0;
            int j = 0;
            for (UIRenderComponent comp : renderObjs) {
                if (j >= numYSquares) {
                    j = 0;
                    i++;
                }

                comp.setPosX(i * this.gridWidth + this.offset.x);
                comp.setPosY( j * this.gridHeight + this.offset.y);
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
