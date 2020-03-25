package com.ui.buttons;

import com.file.Parser;
import com.jade.KeyListener;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.ui.JComponent;
import com.util.Constants;
import org.joml.Vector2f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;

public class ZIndexButton extends JComponent {
    Label label;
    private float debounceTime = 0.2f;
    private float debounceLeft = 0.0f;

    public ZIndexButton() {
        super();
        this.mainComp = new Rectangle(Constants.COLOR_CLEAR);
        this.renderComponents.add(mainComp);

        this.label = new Label(Constants.LARGE_FONT_TEXTURE, "" + Constants.Z_INDEX, new Vector2f(this.mainComp.getPosX(), this.mainComp.getPosY()));
        this.label.setZIndex(3);
        this.renderComponents.addAll(this.label.getRenderComponents());
    }

    @Override
    public void setPosX(float val) {
        this.label.setPosX(val + 13);
        this.mainComp.setPosX(val + 13);
    }

    @Override
    public void setPosY(float val) {
        this.label.setPosY(val + 10);
        this.mainComp.setPosY(val + 10);
    }

    @Override
    public void update(double dt) {
        debounceLeft -= dt;

        if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP) && debounceLeft <= 0.0f) {
            if (Constants.Z_INDEX > -2) {
                Constants.Z_INDEX--;
                this.label.setText("" + (Constants.Z_INDEX * -1));
            }
            debounceLeft = debounceTime;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounceLeft <= 0.0f) {
            if (Constants.Z_INDEX < 2) {
                Constants.Z_INDEX++;
                this.label.setText("" + (Constants.Z_INDEX * -1));
            }
            debounceLeft = debounceTime;
        }
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("ZIndexButton", tabSize));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static ZIndexButton deserialize() {
        Parser.consumeEndObjectProperty();

        return new ZIndexButton();
    }
}
