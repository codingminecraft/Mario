package com.component;

import com.jade.Component;
import com.jade.Window;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

public class SnapToGrid extends Component {
    private float debounceTime = 0.2f;
    private float debounceLeft = 0.0f;

    int gridWidth, gridHeight;

    public SnapToGrid(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    @Override
    public void update(double dt) {
        debounceLeft -= dt;

        if (this.gameObject.getComponent(Sprite.class) != null) {
            float x = (float)Math.floor((Window.getWindow().mouseListener.x + Window.getWindow().getCurrentScene().camera.position.x + Window.getWindow().mouseListener.dx) / gridWidth);
            float y = (float)Math.floor((Window.getWindow().mouseListener.y + Window.getWindow().getCurrentScene().camera.position.y + Window.getWindow().mouseListener.dy) / gridWidth);
            this.gameObject.transform.position.x = x * gridWidth - Window.getWindow().getCurrentScene().camera.position.x;
            this.gameObject.transform.position.y = y * gridWidth - Window.getWindow().getCurrentScene().camera.position.y;

//            if (Window.getWindow().mouseListener.y < Constants.TAB_OFFSET_Y &&
//                Window.getWindow().mouseListener.mousePressed &&
//                Window.getWindow().mouseListener.mouseButton == MouseEvent.BUTTON1 &&
//                debounceLeft < 0) {
//                debounceLeft = debounceTime;
//                GameObject object = gameObject.copy();
//                object.transform.position = new Vector2(x * gridWidth, y * gridHeight);
//                Window.getWindow().getCurrentScene().addGameObject(object);
//            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        Sprite sprite = gameObject.getComponent(Sprite.class);
        if (sprite != null) {
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.drawImage(sprite.image, (int)gameObject.transform.position.x, (int)gameObject.transform.position.y,
                    (int)sprite.width, (int)sprite.height, null);
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
