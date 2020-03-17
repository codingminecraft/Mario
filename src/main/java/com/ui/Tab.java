package com.ui;

import com.file.Parser;
import com.file.Serialize;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import org.joml.Vector2f;

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

        //this.size.x = Constants.FONT_METRICS.stringWidth(this.name) + Constants.TAB_TITLE_PADDING.x * 2;
        //this.size.y = Constants.FONT_METRICS.getHeight();
        //this.centerAdjustment.x = (this.size.x / 2.0f) - (Constants.FONT_METRICS.stringWidth(this.name) / 2.0f);
        this.renderComponent = new Rectangle(Constants.TITLE_BG_COLOR);
        this.renderComponent.setSize(new Vector2f(Constants.DEFAULT_FONT_TEXTURE.getWidthOf(name) + 2 * Constants.TAB_TITLE_PADDING.x, 0.0f));
        this.renderComponent.setZIndex(2);
        this.centerAdjustment.y = 15.0f;

        this.label = new Label(Constants.DEFAULT_FONT_TEXTURE, name,
                new Vector2f(this.renderComponent.getPosX() + Constants.TAB_TITLE_PADDING.x,
                        this.renderComponent.getPosY() + Constants.TAB_TITLE_PADDING.y));
        this.label.setZIndex(3);
    }

    public List<JComponent> getUIElements() {
        return this.components;
    }

    public void setInactive() {
        this.renderComponent.setColor(Constants.TITLE_BG_COLOR);
        this.active = false;
        this.hot = false;

        for (JComponent comp : this.components) {
            comp.renderComponent.setPosition(new Vector2f(-1000, -1000));
        }
    }

    public void setActive() {
        this.renderComponent.setColor(Constants.ACTIVE_TAB);
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setHot() {
        this.hot = true;
        this.renderComponent.setColor(Constants.HOT_TAB);
    }

    public boolean isHot() {
        return this.hot;
    }

    public void setNotHot() {
        this.hot = false;
        this.renderComponent.setColor(Constants.TITLE_BG_COLOR);
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
    public void draw(Graphics2D g2) {
//        if (active) {
//            g2.setColor(Constants.ACTIVE_TAB);
//        } else if (hot) {
//            g2.setColor(Constants.HOT_TAB);
//        } else {
//            g2.setColor(Constants.TITLE_BG_COLOR);
//        }
//
//        g2.fillRect((int)this.position.x, (int)this.position.y, (int)this.size.x, (int)this.size.y);
//        g2.setColor(Color.WHITE);
//        g2.drawString(this.name, this.position.x + centerAdjustment.x, this.position.y + centerAdjustment.y);
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
