package com.ui;

import com.dataStructure.Transform;
import com.file.Parser;
import com.file.Serialize;
import com.jade.*;
import com.renderer.UIRenderComponent;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import com.util.JMath;
import org.joml.Vector2f;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class JWindow extends Serialize {
    private String title;

    // TITLE BAR
    private Vector2f titleBarSize = new Vector2f(0, 20);
    private Vector2f draggableClickSize = new Vector2f(20, 20);
    private float titleBarHeight = 20;

    // TABS
    private List<Tab> tabs;
    private Tab currentTab;

    // SCROLL BAR
    private Vector2f scrollbarPos = new Vector2f(0, 0);
    private Vector2f scrollbarSize = new Vector2f(10, 0);
    private boolean drawScrollbar = false;

    private Vector2f clickedPosDelta = new Vector2f();
    private boolean beingDragged = false;
    private boolean beingResized = false;
    private Cursor resizeCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
    private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    private List<UIRenderComponent> renderComponents;

    private Rectangle mainBackground, titleBar;

    public JWindow(String title, Vector2f pos, Vector2f size) {
        this.title = title;

        this.tabs = new ArrayList<>();
        titleBarSize.x = size.x;
        beginTab(title);

        this.renderComponents = new ArrayList<>();
        this.mainBackground = new Rectangle(Constants.BG_COLOR);
        this.mainBackground.setWidth(size.x);
        this.mainBackground.setHeight(size.y);
        this.renderComponents.add(this.mainBackground);
        this.titleBar = new Rectangle(Constants.TITLE_BG_COLOR);
        this.titleBar.setZIndex(1);
        this.titleBar.setPosY(size.y - titleBarHeight);
        this.titleBar.setWidth(size.x);
        this.titleBar.setHeight(titleBarHeight);
        this.renderComponents.add(this.titleBar);
    }

    public List<UIRenderComponent> getAllRenderComponents() {
        return this.renderComponents;
    }

    public void beginTab(String tabTitle) {
        Tab tab = new Tab(tabTitle);
        this.tabs.add(tab);
        this.currentTab = tab;
    }

    public void endTab() {
        this.currentTab = tabs.get(0);
    }

    public void addUIElement(JComponent uiElement) {
        this.currentTab.addUIElement(uiElement);
    }

    public void start() {
        for (Tab tab : tabs) {
            renderComponents.add(tab.renderComponent);
            for (JComponent comp : tab.getUIElements()) {
                comp.start();
                renderComponents.add(comp.renderComponent);
            }
        }

        tabs.get(0).setActive();
    }

    private boolean mouseInTitleBar() {
        return MouseListener.isDragging() && pointInRectangle(MouseListener.positionScreenCoords(), titleBar.getPosition(), titleBarSize) || beingDragged;
    }
    private boolean mouseInResizeArea() {
        return MouseListener.isDragging() &&
                pointInRectangle(
                        MouseListener.positionScreenCoords(),
                        new Vector2f(this.mainBackground.getPosX() + this.mainBackground.getWidth() - draggableClickSize.x, this.mainBackground.getPosY()),
                        draggableClickSize) ||
                beingResized;
    }
    private void resizeWindowToMousePos() {
        if (!beingResized) {
            //Window.getWindow().setCursor(resizeCursor);
            beingResized = true;
        }
        this.mainBackground.setWidth(MouseListener.positionScreenCoords().x - this.mainBackground.getPosX());
        float newHeight = this.mainBackground.getPosY() + this.mainBackground.getHeight() - MouseListener.positionScreenCoords().y;
        float deltaHeight = newHeight - this.mainBackground.getHeight();
        this.mainBackground.setHeight(newHeight);
        this.mainBackground.setPosY(this.mainBackground.getPosY() - deltaHeight);
        this.titleBar.setWidth(MouseListener.positionScreenCoords().x - this.mainBackground.getPosX());
        scrollbarPos.y = 0;
    }
    private void positionWindowToMousePos() {
        if (!beingDragged) {
            clickedPosDelta.x = MouseListener.positionScreenCoords().x - this.mainBackground.getPosX();
            clickedPosDelta.y = MouseListener.positionScreenCoords().y - this.mainBackground.getPosY();
            beingDragged = true;
        }
        this.mainBackground.setPosX(MouseListener.positionScreenCoords().x - clickedPosDelta.x);
        this.mainBackground.setPosY(MouseListener.positionScreenCoords().y - clickedPosDelta.y);
        this.titleBar.setPosX(this.mainBackground.getPosX());
        this.titleBar.setPosY(this.mainBackground.getPosY() + this.mainBackground.getHeight());
    }
    private void scrollToMouseScrollPos() {
        scrollbarPos.y += MouseListener.getScrollY() * Math.exp(1 / scrollbarSize.y) * 3;
        if (scrollbarPos.y < 0.0f) {
            scrollbarPos.y = 0.0f;
        } else if (scrollbarPos.y + scrollbarSize.y > mainBackground.getPosY() - titleBarSize.y) {
            scrollbarPos.y = mainBackground.getPosY() - titleBarSize.y - scrollbarSize.y;
        }
    }
    private void updateTabs() {
        for (Tab tab : tabs) {
            if (!tab.isActive() && tab.isHot() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                for (Tab oTab : tabs) {
                    if (oTab != tab)
                        oTab.setInactive();
                }
                tab.setActive();
                this.currentTab = tab;
            } else if (!tab.isActive() && tab.mouseInButton()) {
                tab.setHot();
            } else if (!tab.isActive()) {
                tab.setNotHot();
            }
        }
    }
    private void updateComponents(double dt) {
        if (!beingResized && !beingDragged) {
            for (JComponent comp : this.currentTab.getUIElements()) {
                comp.update(dt);
            }
        }
    }

    public void update(double dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            // Mouse was clicked, is it inside the window title bar and dragging?
            if (mouseInTitleBar()) {
                positionWindowToMousePos();
            } else if (mouseInResizeArea()) {
                resizeWindowToMousePos();
            }
        } else if (pointInWindow(MouseListener.positionScreenCoords()) && MouseListener.getScrollY() != 0.0f && drawScrollbar) {
            scrollToMouseScrollPos();
        } else {
            beingDragged = false;
            if (beingResized) {
                //Window.getWindow().setCursor(normalCursor);
            }
            beingResized = false;
        }

        updateTabs();
        updateComponents(dt);
        positionElements();
    }

    private void positionElements() {
        // First position all tabs
        int currentX = (int)Constants.MARGIN.x;
        for (int i=0; i < tabs.size(); i++) {
            tabs.get(i).renderComponent.setPosX(currentX + this.titleBar.getPosX());
            tabs.get(i).renderComponent.setPosY(this.titleBar.getPosY());
            tabs.get(i).renderComponent.setHeight(titleBar.getHeight());
            currentX += tabs.get(i).renderComponent.getWidth() + Constants.MARGIN.x;
        }
//
//        currentX = (int)Constants.PADDING.x;
//        int currentY = (int)(titleBarSize.y + Constants.PADDING.y);
//        int rowHeight = 0;
//        // First pass, position everything roughly and calculate total height
//        for (JComponent comp : this.currentTab.getUIElements()) {
//            comp.transform.position.x = this.position.x + currentX;
//            if (comp.transform.position.y > rowHeight) rowHeight = (int)Math.ceil(comp.transform.position.y);
//            if (comp.transform.position.x + comp.transform.scale.x > this.position.x + size.x || comp.isLineBreak) {
//                comp.transform.position.x = this.position.x + Constants.PADDING.x;
//                currentY += rowHeight + Constants.PADDING.y;
//                currentX = (int)Constants.PADDING.x;
//                rowHeight = 0;
//                if (comp.isLineBreak) continue;
//            }
//
//            if (comp.isCentered) {
//                comp.transform.position.x = ((size.x - currentX) / 2.0f) - (comp.transform.scale.x / 2.0f) + position.x;
//            }
//
//            comp.transform.position.y = this.position.y + currentY;
//            currentX += comp.transform.scale.x + Constants.PADDING.x;
//        }
//
//        float totalHeight = currentY + rowHeight;
//        if (totalHeight > size.y) {
//            drawScrollbar = true;
//        } else {
//            drawScrollbar = false;
//            scrollbarPos.y = 0;
//        }
//
//        // Second pass adjust according to position of scrollbar
//        for (JComponent comp : this.currentTab.getUIElements()) {
//            float percentOfHeight = 1 / ((size.y - titleBarSize.y) / totalHeight);
//            comp.transform.position.y -= (scrollbarPos.y * percentOfHeight) + 1;
//
//            if (comp.transform.position.y >= this.position.y + titleBarSize.y && comp.transform.position.y + comp.transform.scale.y <= this.position.y + size.y) {
//                comp.visible = true;
//                comp.draw(g2);
//            } else {
//                comp.visible = false;
//            }
//        }
//
//        if (drawScrollbar) {
//            g2.setColor(Color.BLACK);
//            float percentOfHeight = size.y / totalHeight;
//            float height = percentOfHeight * (size.y - titleBarSize.y);
//            scrollbarSize.y = height;
//            g2.fillRect((int)(position.x + size.x - scrollbarSize.x), (int)(titleBarHeight + scrollbarPos.y + position.y), (int)scrollbarSize.x, (int)height);
//        }
    }

    private boolean pointInRectangle(Vector2f point, Vector2f rectPos, Vector2f rectSize) {
        return point.x > rectPos.x && point.x < rectPos.x + rectSize.x && point.y > rectPos.y &&
                point.y < rectPos.y + rectSize.y;
    }

    public boolean pointInWindow(Vector2f point) {
        return pointInRectangle(point, this.mainBackground.getPosition(), this.mainBackground.getSize());
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        // JWindow
        builder.append(Serialize.beginObjectProperty("JWindow", tabSize));

        // Title
        builder.append(Serialize.addStringProperty("Title", title, tabSize + 1, true, true));

        // Position
        builder.append(Serialize.beginObjectProperty("Position", tabSize + 1));
        builder.append(JMath.serialize(this.mainBackground.getPosition(), tabSize + 2));
        builder.append(Serialize.closeObjectProperty(tabSize + 1));
        builder.append(Serialize.addEnding(true, true));

        // Size
        builder.append(Serialize.beginObjectProperty("Size", tabSize + 1));
        builder.append(JMath.serialize(this.mainBackground.getSize(), tabSize + 2));
        builder.append(Serialize.closeObjectProperty(tabSize + 1));
        builder.append(Serialize.addEnding(true, true));

        // Tabs
        builder.append(Serialize.beginObjectProperty("Tabs", tabSize + 1));
        for (int i=0; i < tabs.size(); i++) {
            builder.append(tabs.get(i).serialize(tabSize + 2));

            if (i < tabs.size() - 1) {
                builder.append(Serialize.addEnding(true, true));
            } else {
                builder.append(Serialize.addEnding(true, false));
            }
        }
        builder.append(Serialize.addEnding(true, false));

        // End JWindow object
        builder.append(Serialize.closeObjectProperty(tabSize + 1));
        builder.append(Serialize.addEnding(true, false));
        builder.append(Serialize.closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static JWindow deserialize() {
        Parser.consumeBeginObjectProperty("JWindow");

        // TITLE
        String title = Parser.consumeStringProperty("Title");
        Parser.consume(',');

        // POSITION AND SIZE
        Parser.consumeBeginObjectProperty("Position");
        Vector2f position = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        Parser.consumeBeginObjectProperty("Size");
        Vector2f size = JMath.deserializeVector2f();
        Parser.consumeEndObjectProperty();
        Parser.consume(',');

        JWindow window = new JWindow(title, position, size);

        // TABS
        Parser.consume(',');
        Parser.consumeBeginObjectProperty("Tabs");
        // Remove the default tab created by the window, and then add the appropriate deserialized tab
        Tab firstTab = Tab.deserialize();
        window.tabs.remove(0);
        window.tabs.add(firstTab);
        window.currentTab = firstTab;

        while (Parser.peek() == ',') {
            Parser.consume(',');
            Tab tab = Tab.deserialize();
            window.tabs.add(tab);
        }

        Parser.consumeEndObjectProperty();
        Parser.consumeEndObjectProperty();

        return window;
    }
}
