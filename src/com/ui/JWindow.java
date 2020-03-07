package com.ui;

import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.file.Serialize;
import com.jade.*;
import com.util.Constants;

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
                scrollbarPos.y = 0;
            }
        } else if (pointInWindow(Window.mouseListener().position) && Window.mouseListener().mouseWheel.y != 0.0f && drawScrollbar) {
            scrollbarPos.y += Window.mouseListener().mouseWheel.y * Math.exp(1 / scrollbarSize.y) * 3;
            if (scrollbarPos.y < 0.0f) {
                scrollbarPos.y = 0.0f;
            } else if (scrollbarPos.y + scrollbarSize.y > size.y - titleBarSize.y) {
                scrollbarPos.y = size.y - titleBarSize.y - scrollbarSize.y;
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
        g2.setColor(Constants.BG_COLOR);
        g2.fillRect((int)position.x, (int)position.y, (int)size.x, (int)size.y);
        g2.setColor(Constants.TITLE_BG_COLOR);
        g2.fillRect((int)position.x, (int)position.y, (int)size.x, (int)titleBarHeight);
        g2.setColor(Color.WHITE);
        g2.drawString(title, position.x + 5, position.y + 15);

        int currentX = (int)Constants.PADDING.x;
        int currentY = (int)(titleBarSize.y + Constants.PADDING.y);
        int rowHeight = 0;
        // First pass, position everything roughly and calculate total height
        for (JComponent comp : elements) {
            comp.position.x = this.position.x + currentX;
            if (comp.size.y > rowHeight) rowHeight = (int)Math.ceil(comp.size.y);
            if (comp.position.x + comp.size.x > this.position.x + size.x || comp.isLineBreak) {
                comp.position.x = this.position.x + Constants.PADDING.x;
                currentY += rowHeight + Constants.PADDING.y;
                currentX = (int)Constants.PADDING.x;
                rowHeight = 0;
                if (comp.isLineBreak) continue;
            }

            if (comp.isCentered) {
                comp.position.x = ((size.x - currentX) / 2.0f) - (comp.size.x / 2.0f) + position.x;
            }

            comp.position.y = this.position.y + currentY;
            currentX += comp.size.x + Constants.PADDING.x;
        }

        float totalHeight = currentY + rowHeight;
        if (totalHeight > size.y) {
            drawScrollbar = true;
        } else {
            drawScrollbar = false;
            scrollbarPos.y = 0;
        }

        // Second pass adjust according to position of scrollbar
        for (JComponent comp : elements) {
            float percentOfHeight = 1 / ((size.y - titleBarSize.y) / totalHeight);
            comp.position.y -= (scrollbarPos.y * percentOfHeight) + 1;

            if (comp.position.y >= this.position.y + titleBarSize.y && comp.position.y + comp.size.y <= this.position.y + size.y) {
                comp.visible = true;
                comp.draw(g2);
            } else {
                comp.visible = false;
            }
        }

        if (drawScrollbar) {
            g2.setColor(Color.BLACK);
            float percentOfHeight = size.y / totalHeight;
            float height = percentOfHeight * (size.y - titleBarSize.y);
            scrollbarSize.y = height;
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
