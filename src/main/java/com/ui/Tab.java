package com.ui;

import com.file.Parser;
import com.file.Serialize;
import com.renderer.UIRenderComponent;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Tab extends JComponent {
    private String name;
    private List<JComponent> components;
    public Label label;
    private boolean active = false;
    private boolean hot = false;
    private Vector2f centerAdjustment = new Vector2f();

    public Tab(String name) {
        this.name = name;
        this.components = new ArrayList<>();

        this.mainComp = new Rectangle(Constants.TITLE_BG_COLOR, new Vector4f(7, 7, 0, 0), Constants.TITLE_BG_COLOR, 0.1f);
        this.mainComp.setSize(new Vector2f(Constants.DEFAULT_FONT_TEXTURE.getWidthOf(name) + 2 * Constants.TAB_TITLE_PADDING.x, 0.0f));
        this.mainComp.setZIndex(2);
        this.renderComponents.add(mainComp);
        this.centerAdjustment.y = 15.0f;

        this.label = new Label(Constants.DEFAULT_FONT_TEXTURE, name,
                new Vector2f(this.mainComp.getPosX() + Constants.TAB_TITLE_PADDING.x,
                        this.mainComp.getPosY() + Constants.TAB_TITLE_PADDING.y));
        this.label.setZIndex(3);
        this.renderComponents.addAll(this.label.getRenderComponents());
    }

    public List<JComponent> getUIElements() {
        return this.components;
    }

    public void setInactive() {
        this.mainComp.setColor(Constants.TITLE_BG_COLOR);
        this.active = false;
        this.hot = false;

        for (JComponent comp : this.components) {
            for (UIRenderComponent renderComponent : comp.getRenderComponents()) {
                renderComponent.setPosition(new Vector2f(-1000, -1000));
            }
        }
    }

    public void setActive() {
        this.mainComp.setColor(Constants.ACTIVE_TAB);
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setHot() {
        this.hot = true;
        this.mainComp.setColor(Constants.HOT_TAB);
    }

    public boolean isHot() {
        return this.hot;
    }

    public void setNotHot() {
        this.hot = false;
        this.mainComp.setColor(Constants.TITLE_BG_COLOR);
    }

    public JComponent getJComponent(int id) {
        for (JComponent comp : components) {
            if (comp.id == id) {
                return comp;
            }
        }

        return null;
    }

    public void addUIElement(JComponent comp) {
        comp.parent = this;
        this.components.add(comp);
    }

    @Override
    public void update(double dt) {
        for (JComponent comp : components) {
            comp.update(dt);
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(Serialize.beginObjectProperty("Tab", tabSize));

        if (components.size() > 0) {
            builder.append(Serialize.addStringProperty("Name", this.name, tabSize + 1, true, true));
        } else {
            builder.append(Serialize.addStringProperty("Name", this.name, tabSize + 1, true, false));
        }

        builder.append(Serialize.beginObjectProperty("Components", tabSize + 1));
        int i = 0;
        for (JComponent c : components) {
            String str = c.serialize(tabSize + 2);
            if (str.compareTo("") != 0) {
                builder.append(str);
                if (i != components.size() - 1) {
                    builder.append(addEnding(true, true));
                } else {
                    builder.append(addEnding(true, false));
                }
            }
            i++;
        }
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, false));

        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static Tab deserialize() {
        Parser.consumeBeginObjectProperty("Tab");
        String name = Parser.consumeStringProperty("Name");

        Tab tab = new Tab(name);
        if (Parser.peek() == ',') {
            Parser.consume(',');
            Parser.consumeBeginObjectProperty("Components");
            tab.addUIElement(Parser.parseJComponent());
            while (Parser.peek() == ',') {
                Parser.consume(',');
                tab.addUIElement(Parser.parseJComponent());
            }
            Parser.consumeEndObjectProperty();
        }

        Parser.consumeEndObjectProperty();

        return tab;
    }
}
