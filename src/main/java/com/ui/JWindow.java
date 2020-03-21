package com.ui;

import com.file.Parser;
import com.file.Serialize;
import com.jade.*;
import com.renderer.UIRenderComponent;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import com.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class JWindow extends Serialize {
    private String title;

    // TITLE BAR
    private Vector2f draggableClickSize = new Vector2f(20, 20);
    private float titleBarHeight = 20;

    // TABS
    private List<Tab> tabs;
    private Tab currentTab;

    // SCROLL BAR
    private boolean drawScrollbar = false;
    private float localScrollbarY = 0.0f;

    private Vector2f clickedPosDelta = new Vector2f();
    private boolean beingDragged = false;
    private boolean beingResized = false;

    private List<UIRenderComponent> renderComponents;

    private Rectangle mainBackground, titleBar, scrollbar;

    public JWindow(String title, Vector2f pos, Vector2f size) {
        this.title = title;

        this.tabs = new ArrayList<>();
        beginTab(title);

        this.renderComponents = new ArrayList<>();
        this.mainBackground = new Rectangle(Constants.BG_COLOR, new Vector4f(5, 5, 5, 5), Constants.BG_COLOR, 0.1f);
        this.mainBackground.setWidth(size.x);
        this.mainBackground.setHeight(size.y);
        this.mainBackground.setPosition(pos);
        this.renderComponents.add(this.mainBackground);

        this.titleBar = new Rectangle(Constants.TITLE_BG_COLOR, new Vector4f(5, 5, 0, 0), Constants.TITLE_BG_COLOR, 0.1f);
        this.titleBar.setZIndex(1);
        this.titleBar.setPosX(mainBackground.getPosX());
        this.titleBar.setPosY(mainBackground.getPosY() + size.y - titleBarHeight);
        this.titleBar.setWidth(size.x);
        this.titleBar.setHeight(titleBarHeight);
        this.renderComponents.add(this.titleBar);

        this.scrollbar = new Rectangle(Constants.HOT_TAB);
        this.scrollbar.setZIndex(1);
        this.scrollbar.setBorderRadius(4.0f);
        this.scrollbar.setBorderWidth(0.1f);
        this.scrollbar.setBorderColor(Constants.HOT_TAB);
        this.scrollbar.setPosX(-1000);
        this.scrollbar.setWidth(10);
        this.renderComponents.add(this.scrollbar);
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
        uiElement.mainComp.setPosX(-1000);
        this.currentTab.addUIElement(uiElement);
    }

    public void start() {
        tabs.get(0).setActive();
        positionElements();

        for (Tab tab : tabs) {
            renderComponents.addAll(tab.getRenderComponents());
            for (JComponent comp : tab.getUIElements()) {
                comp.start();
                renderComponents.addAll(comp.getRenderComponents());
            }
        }
    }

    private boolean mouseInTitleBar() {
        return MouseListener.isDragging() && pointInRectangle(MouseListener.positionScreenCoords(), titleBar.getPosition(), titleBar.getSize()) || beingDragged;
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
        scrollbar.setPosY(0.0f);

        positionElements();
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
        this.titleBar.setPosY(this.mainBackground.getPosY() + this.mainBackground.getHeight() - this.titleBar.getHeight());

        positionElements();
    }
    private void scrollToMouseScrollPos() {
        localScrollbarY -= (float)(MouseListener.getScrollY() * Math.exp(1 / scrollbar.getHeight()) * 3);

        if (localScrollbarY < 0.0f) {
            localScrollbarY = 0.0f;
        } else if (localScrollbarY + scrollbar.getHeight() > mainBackground.getHeight() - titleBar.getHeight() - scrollbar.getHeight()) {
            localScrollbarY = mainBackground.getHeight() - titleBar.getHeight() - scrollbar.getHeight();
        }
        positionElements();
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

                positionElements();
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
    }

    private void positionElements() {
        // First position all tabs
        int currentX = (int)Constants.MARGIN.x;
        for (int i=0; i < tabs.size(); i++) {
            tabs.get(i).mainComp.setPosX(currentX + this.titleBar.getPosX());
            tabs.get(i).mainComp.setPosY(this.titleBar.getPosY());
            tabs.get(i).mainComp.setHeight(titleBar.getHeight() * 0.8f);
            tabs.get(i).label.setPosX(currentX + this.titleBar.getPosX() + Constants.TAB_TITLE_PADDING.x);
            tabs.get(i).label.setPosY(this.titleBar.getPosY() + Constants.TAB_TITLE_PADDING.y);
            currentX += tabs.get(i).mainComp.getWidth() + Constants.MARGIN.x;
        }

        currentX = (int)Constants.PADDING.x;
        int currentY = (int)(-(titleBar.getHeight() * 3) - Constants.PADDING.y);
        int rowHeight = 0;
        // First pass, position everything roughly and calculate total height
        for (JComponent comp : this.currentTab.getUIElements()) {
            comp.visible = true;

            comp.setPosX(this.mainBackground.getPosX() + currentX);
            if (comp.getHeight() > rowHeight) rowHeight = (int)Math.ceil(comp.getHeight());
            if (comp.getPosX() + comp.getWidth() > mainBackground.getPosX() + mainBackground.getWidth() || comp.isLineBreak) {
                comp.setPosX(mainBackground.getPosX() + Constants.PADDING.x);
                currentY -= rowHeight + Constants.PADDING.y;
                currentX = (int)Constants.PADDING.x;
                rowHeight = 0;
                if (comp.isLineBreak) continue;
            }

            if (comp.isCentered) {
                comp.setPosX(((mainBackground.getWidth() - currentX) / 2.0f) - (comp.getWidth() / 2.0f) + mainBackground.getPosX());
            }

            comp.setPosY(mainBackground.getPosY() + mainBackground.getHeight() + currentY);
            currentX += comp.getWidth() + Constants.PADDING.x;
        }

        float totalHeight = -(currentY - rowHeight);
        if (totalHeight > mainBackground.getHeight()) {
            drawScrollbar = true;
        } else {
            drawScrollbar = false;
            scrollbar.setPosY(0.0f);
        }

        if (drawScrollbar) {
            // Second pass adjust according to position of scrollbar
            for (JComponent comp : this.currentTab.getUIElements()) {

                float percentOfHeight = 1 / ((mainBackground.getHeight() - titleBar.getHeight()) / totalHeight);
                comp.setPosY(comp.getPosY() + ((localScrollbarY * percentOfHeight) + 1));

                if (comp.getPosY() >= mainBackground.getPosY() + titleBar.getHeight() &&
                        comp.getPosY() + comp.getHeight() <= mainBackground.getPosY() + mainBackground.getHeight()) {
                    comp.visible = true;
                } else {
                    comp.visible = false;
                    comp.mainComp.setPosX(-1000);
                }
            }

            float percentOfHeight = mainBackground.getHeight() / totalHeight;
            float height = percentOfHeight * (mainBackground.getHeight() - titleBar.getHeight());
            scrollbar.setHeight(height);
            scrollbar.setPosX(mainBackground.getPosX() + mainBackground.getWidth() - scrollbar.getWidth());
            scrollbar.setPosY(mainBackground.getPosY() + mainBackground.getHeight() - titleBar.getHeight() - scrollbar.getHeight() - localScrollbarY);
        }
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
