package com.jade;

import com.dataStructure.Vector2;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private Vector2 position = new Vector2();
    private boolean mouseButtonPressed[] = new boolean[4];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().position.x = (float)xpos;
        get().position.y = (float)ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2] || get().mouseButtonPressed[3];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xoffset, double yoffset) {
        get().scrollX = xoffset;
        get().scrollY = yoffset;
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }

        return instance;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float)get().xPos;
    }
    public static float getY() {
        return (float)get().yPos;
    }
    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }
    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }
    public static float getScrollX() {
        return (float)get().scrollX;
    }
    public static float getScrollY() {
        return (float)get().scrollY;
    }
    public static boolean isDragging() {
        return get().isDragging;
    }
    public static Vector2 position() {
        return get().position;
    }
    public static boolean mouseButtonDown(int button) {
        return get().mouseButtonPressed[button];
    }
}
