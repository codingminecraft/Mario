package com.jade;

import com.file.Parser;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;

public class LevelScene extends Scene {

    public LevelScene(String name) {
        super.Scene(name);
    }

    @Override
    public void init() {
        initAssetPool();
    }


    public void initAssetPool() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        renderer.render(g2);
    }
}
