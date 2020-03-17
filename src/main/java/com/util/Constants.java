package com.util;

import com.renderer.fonts.FontTexture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class Constants {
    public static final int SCREEN_WIDTH = 32 * 32;
    public static final int SCREEN_HEIGHT = 32 * 18;
    public static final String SCREEN_TITLE = "Jade";
    public static final RenderingHints ANTIALIASING_HINT = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON );
    public static final RenderingHints NO_ANTIALIASING_HINT = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF );

    public static final float GRAVITY = 2850;
    public static final float TERMINAL_VELOCITY = 1900;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int TILES_MAX_X = 200;

    public static FontMetrics FONT_METRICS;
    public static String CURRENT_LEVEL = "";
    public static int Z_INDEX = 0;

    // =========================================================================
    // GUI Colors
    // =========================================================================
    public static final Vector4f BG_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 0.5f);
    public static final Vector4f TITLE_BG_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Vector4f BUTTON_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Vector4f ACTIVE_TAB = new Vector4f(0.26f, 0.59f, 0.98f, 1.00f);
    public static final Vector4f HOT_TAB = new Vector4f(0.26f, 0.59f, 0.98f, 0.80f);
    public static final Vector4f CLEAR_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

    // =========================================================================
    // GUI constraints
    // =========================================================================
    public static final Vector2f PADDING = new Vector2f(6, 6);
    public static final Vector2f TAB_TITLE_PADDING = new Vector2f(10, 0);
    public static final Vector2f MARGIN = new Vector2f(4, 0);

    public static final FontTexture DEFAULT_FONT_TEXTURE = new FontTexture(new Font("Arial", Font.PLAIN, 14), "US-ASCII");
}
