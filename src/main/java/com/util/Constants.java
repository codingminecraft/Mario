package com.util;

import com.dataStructure.Vector2;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class Constants {
    public static final int SCREEN_WIDTH = 2560;
    public static final int SCREEN_HEIGHT = 1440;
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
    public static final Color BG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    public static final Color TITLE_BG_COLOR = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color BUTTON_COLOR = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color ACTIVE_TAB = new Color(0.26f, 0.59f, 0.98f, 1.00f);
    public static final Color HOT_TAB = new Color(0.26f, 0.59f, 0.98f, 0.80f);

    // =========================================================================
    // GUI constraints
    // =========================================================================
    public static final Vector2 PADDING = new Vector2(6, 6);
    public static final Vector2 TAB_TITLE_PADDING = new Vector2(10, 0);
    public static final Vector2 MARGIN = new Vector2(4, 0);
}
