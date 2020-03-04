package com.ui;

import com.component.Sprite;
import com.dataStructure.Vector2;
import com.jade.Component;
import com.jade.Window;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class Button extends Component {
    public static int ACTIVE_ITEM = -1;
    private static int MAX_ID = 0;

    private boolean hot = false;
    private boolean active = false;
    private Sprite sprite;
    private int id;

    private Vector2 position, size;

    public Button(Sprite sprite, Vector2 position, Vector2 size) {
        this.sprite = sprite;
        this.position = position;
        this.size = size;
        this.id = Button.MAX_ID;
        this.MAX_ID++;
    }

    @Override
    public void update(double dt) {
        this.hot = Window.mouseListener().x > this.position.x && Window.mouseListener().x < this.position.x + this.size.x &&
                Window.mouseListener().y > this.position.y && Window.mouseListener().y < this.position.y + this.size.y;

        if (!active && hot) {
            if (Window.mouseListener().mousePressed && Window.mouseListener().mouseButton == MouseEvent.BUTTON1) {
                active = true;
                ACTIVE_ITEM = this.id;
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
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
