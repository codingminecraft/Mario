package com.component;

import com.dataStructure.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    public List<Sprite> sprites;
    public int tileWidth;
    public int tileHeight;
    public int spacing;

    public int overallWidth;
    public int overallHeight;

    public Spritesheet(String pictureFile, int tileWidth, int tileHeight, int spacing, int columns, int size) {
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.spacing = spacing;

        Sprite parent = AssetPool.getSprite(pictureFile);
        sprites = new ArrayList<>();
        overallWidth = parent.image.getWidth();
        overallHeight = parent.image.getHeight();

        int row = 0;
        int count = 0;
        while (count < size) {
            for (int column = 0; column < columns; column++) {
                int imgX = (column * tileWidth) + (column * spacing);
                int imgY = (row * tileHeight) + (row * spacing);

                sprites.add(new Sprite(parent.image.getSubimage(imgX, imgY, tileWidth, tileHeight),
                        row, column, count, pictureFile));
                count++;
                if (count > size - 1) {
                    break;
                }
            }
            row++;
        }
    }
}
