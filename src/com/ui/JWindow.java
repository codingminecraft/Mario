package com.ui;

import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.file.Serialize;
import com.jade.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class JWindow extends Serialize {
    private List<JComponent> elements;
    private Vector2 position, size;
    private String title;

    // TITLE BAR
    private Vector2 titleBarSize = new Vector2(0, 20);
    private Vector2 draggableClickSize = new Vector2(20, 20);
    private float titleBarHeight = 20;

    // SCROLL BAR
    private Vector2 scrollbarPos = new Vector2(0, 0);
    private Vector2 scrollbarSize = new Vector2(10, 0);
    private boolean drawScrollbar = false;

    private final Color BG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    private final Color TITLE_BG_COLOR = new Color(0.3f, 0.3f, 0.3f, 1.0f);

    private Vector2 clickedPosDelta = new Vector2();
    private boolean beingDragged = false;
    private boolean beingResized = false;
    private Cursor resizeCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
    private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    public JWindow(String title, Vector2 pos, Vector2 size) {
        this.title = title;
        this.position = pos;
        this.size = size;
        this.elements = new ArrayList<>();
        titleBarSize.x = this.size.x;
    }

    public void addUIElement(JComponent uiElement) {
        uiElement.parent = this;
        this.elements.add(uiElement);
    }

    public JComponent getJComponent(int id) {
        for (JComponent comp : elements) {
            if (comp.id == id) {
                return comp;
            }
        }

        return null;
    }

    public void start() {
        for (JComponent comp : elements) {
            comp.start();
        }
    }

    public void update(double dt) {
        if (Window.mouseListener().mousePressed && Window.mouseListener().mouseButton == MouseEvent.BUTTON1) {
            // Mouse was clicked, is it inside the window title bar and dragging?
            if (Window.mouseListener().mouseDragged && pointInRectangle(Window.mouseListener().position, this.position, titleBarSize) ||
                beingDragged) {
                if (!beingDragged) {
                    clickedPosDelta.x = Window.mouseListener().x - this.position.x;
                    clickedPosDelta.y = Window.mouseListener().y - this.position.y;
                    beingDragged = true;
                }
                this.position.x = Window.mouseListener().x - clickedPosDelta.x;
                this.position.y = Window.mouseListener().y - clickedPosDelta.y;
            } else if (Window.mouseListener().mouseDragged &&
                    pointInRectangle(Window.mouseListener().position, Vector2.minus(Vector2.add(this.position, this.size), this.draggableClickSize), draggableClickSize) ||
                    beingResized) {
                if (!beingResized) {
                    Window.getWindow().setCursor(resizeCursor);
                    beingResized = true;
                }
                this.size.x = Window.mouseListener().x - this.position.x;
                this.size.y = Window.mouseListener().y - this.position.y;
            }
        } else {
            beingDragged = false;
            if (beingResized) {
                Window.getWindow().setCursor(normalCursor);
            }
            beingResized = false;
        }

        if (!beingResized && !beingDragged) {
            for (JComponent comp : elements) {
                comp.update(dt);
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(BG_COLOR);
        g2.fillRect((int)position.x, (int)position.y, (int)size.x, (int)size.y);
        g2.setColor(TITLE_BG_COLOR);
        g2.fillRect((int)position.x, (int)position.y, (int)size.x, (int)titleBarHeight);
        g2.setColor(Color.WHITE);
        g2.drawString(title, position.x + 5, position.y + 15);

        int currentX = 0;
        int currentY = (int)titleBarSize.y + 2 + (int)scrollbarPos.y;
        int rowHeight = 0;
        for (JComponent comp : elements) {
            comp.position.x = this.position.x + currentX;
            if (comp.size.y > rowHeight) rowHeight = (int)Math.ceil(comp.size.y);
            if (comp.position.x + comp.size.x > this.position.x + size.x) {
                comp.position.x = this.position.x + 2;
                currentY += rowHeight;
                currentX = 0;
                rowHeight = 0;
            }

            comp.position.y = this.position.y + currentY - scrollbarPos.y;

            if (comp.position.y >= this.position.y && comp.position.y + comp.size.y <= this.position.y + size.y)
                comp.draw(g2);

            if (currentY + rowHeight >= size.y) {
                drawScrollbar = true;
            } else {
                drawScrollbar = false;
            }
            currentX += comp.size.x + 2;
        }

        currentY += rowHeight;
        if (drawScrollbar) {
            g2.setColor(Color.BLACK);
            float percentOfHeight = size.y / currentY;
            float height = percentOfHeight * (size.y - titleBarSize.y);
            g2.fillRect((int)(position.x + size.x - scrollbarSize.x), (int)(titleBarHeight + scrollbarPos.y + position.y), (int)scrollbarSize.x, (int)height);
        }
    }

    private boolean pointInRectangle(Vector2 point, Vector2 rectPos, Vector2 rectSize) {
        return point.x > rectPos.x && point.x < rectPos.x + rectSize.x && point.y > rectPos.y &&
                point.y < rectPos.y + rectSize.y;
    }

    public boolean pointInWindow(Vector2 point) {
        return pointInRectangle(point, this.position, this.size);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        // JWindow
        builder.append(beginObjectProperty("JWindow", tabSize));

        // Title
        builder.append(addStringProperty("Title", title, tabSize + 1, true, true));

        // Position
        builder.append(beginObjectProperty("Position", tabSize + 1));
        builder.append(position.serialize(tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        // Size
        builder.append(beginObjectProperty("Size", tabSize + 1));
        builder.append(size.serialize(tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));

        // JComponents
        if (elements.size() > 0) {
            builder.append(addEnding(true, true));
            builder.append(beginObjectProperty("JComponents", tabSize + 1));
        } else {
            builder.append(addEnding(true, false));
        }

        int i = 0;
        for (JComponent c : elements) {
            String str = c.serialize(tabSize + 2);
            if (str.compareTo("") != 0) {
                builder.append(str);
                if (i != elements.size() - 1) {
                    builder.append(addEnding(true, true));
                } else {
                    builder.append(addEnding(true, false));
                }
            }
            i++;
        }

        // End JComponents if applicable
        if (elements.size() > 0) {
            builder.append(closeObjectProperty(tabSize + 1));
        }

        // End JWindow object
        builder.append(addEnding(true, false));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static JWindow deserialize() {
        Parser.consumeBeginObjectProperty("JWindow");

        String title = Parser.consumeStringProperty("Title");
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Position");
        Vector2 position = Vector2.deserialize();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2 size = Vector2.deserialize();
        Parser.consumeEndObjectProperty();

        JWindow window = new JWindow(title, position, size);

        if (Parser.peek() == ',') {
            Parser.consume(',');
            Parser.consumeBeginObjectProperty("JComponents");
            window.addUIElement(Parser.parseJComponent());

            while (Parser.peek() == ',') {
                Parser.consume(',');
                window.addUIElement(Parser.parseJComponent());
            }
            Parser.consumeEndObjectProperty();
        }
        Parser.consumeEndObjectProperty();

        return window;
    }
}
