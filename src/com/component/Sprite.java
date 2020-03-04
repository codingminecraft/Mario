package com.component;

import com.dataStructure.AssetPool;
import com.file.Parser;
import com.jade.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Sprite extends Component {

    public BufferedImage image;
    public String pictureFile;
    public int width, height;

    public boolean isSubsprite = false;
    public int row, column, index;

    public Sprite(String pictureFile) {
        this.pictureFile = pictureFile;

        try {
            File file = new File(pictureFile);

            if (AssetPool.hasSprite(pictureFile)) {
                throw new Exception("Sprite: Asset already exists: " + pictureFile);
            }

            this.image = ImageIO.read(file);
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public Sprite(BufferedImage image, String pictureFile) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pictureFile = pictureFile;
    }

    public Sprite(BufferedImage image, int row, int column, int index, String pictureFile) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.row = row;
        this.column = column;
        this.index = index;
        this.isSubsprite = true;
        this.pictureFile = pictureFile;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int)gameObject.transform.position.x,
                (int)gameObject.transform.position.y, (int)(width * gameObject.transform.scale.x), (int)(height * gameObject.transform.scale.y), null);
    }

    @Override
    public Component copy() {
        if (!isSubsprite)
            return new Sprite(this.image, pictureFile);
        else
            return new Sprite(this.image, this.row, this.column, this.index, pictureFile);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Sprite", tabSize));
        builder.append(addBooleanProperty("isSubsprite", isSubsprite, tabSize + 1, true, true));

        if (isSubsprite) {
            builder.append(addStringProperty("FilePath", pictureFile, tabSize + 1, true, true));
            builder.append(addIntProperty("row", row, tabSize + 1, true, true));
            builder.append(addIntProperty("column", column, tabSize + 1, true, true));
            builder.append(addIntProperty("index", index, tabSize + 1, true, false));
            builder.append(closeObjectProperty(tabSize));

            return builder.toString();
        }

        builder.append(addStringProperty("FilePath", pictureFile, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static Sprite deserialize() {
        boolean isSubsprite = Parser.consumeBooleanProperty("isSubsprite");
        Parser.consume(',');
        String filePath = Parser.consumeStringProperty("FilePath");

        if (isSubsprite) {
            Parser.consume(',');
            Parser.consumeIntProperty("row");
            Parser.consume(',');
            Parser.consumeIntProperty("column");
            Parser.consume(',');
            int index = Parser.consumeIntProperty("index");
            if (!AssetPool.hasSpritesheet(filePath)) {
                System.out.println("Spritesheet '" + filePath + "' has not been loaded yet!");
                System.exit(-1);
            }
            Parser.consumeEndObjectProperty();
            return (Sprite)AssetPool.getSpritesheet(filePath).sprites.get(index).copy();
        }

        if (!AssetPool.hasSprite(filePath)) {
            System.out.println("Sprite '" + filePath + "' has not been loaded yet!");
            System.exit(-1);
        }

        Parser.consumeEndObjectProperty();
        return (Sprite)AssetPool.getSprite(filePath).copy();
    }
}
