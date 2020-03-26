package com.dataStructure;

import com.component.Sprite;
import com.component.Spritesheet;
import com.jade.Sound;
import com.renderer.Shader;
import com.renderer.Texture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetPool {
    static Map<String, Sprite> sprites = new HashMap<>();
    static Map<String, Spritesheet> spritesheets = new HashMap<>();
    static Map<String, Shader> shaders = new HashMap<>();
    static Map<String, Texture> textures = new HashMap<>();
    static Map<String, Sound> sounds = new HashMap<>();

    public static boolean hasSprite(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.sprites.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasSpritesheet(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.spritesheets.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasShader(String shaderPath) {
        File tmp = new File(shaderPath);
        return AssetPool.shaders.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasTexture(String pictureFile) {
        File tmp = new File(pictureFile);
        return AssetPool.textures.containsKey(tmp.getAbsolutePath());
    }

    public static boolean hasSound(String soundFile) {
        File tmp = new File(soundFile);
        return AssetPool.sounds.containsKey(tmp.getAbsolutePath());
    }

    public static Texture getTexture(String pictureFile) {
        File file = new File(pictureFile);
        if (hasTexture(pictureFile)) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(file.getAbsolutePath());
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return AssetPool.textures.get(file.getAbsolutePath());
        }
    }

    public static Sound getSound(String soundFile) {
        File file = new File(soundFile);
        if (hasSound(soundFile)) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added '" + soundFile + "'.";
        }
        return null;
    }

    public static Sound addSound(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (hasSound(soundFile)) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return AssetPool.sounds.get(file.getAbsolutePath());
        }
    }

    public static Sprite getSprite(String pictureFile) {
        File file = new File(pictureFile);
        if (AssetPool.hasSprite(file.getAbsolutePath())) {
            return AssetPool.sprites.get(file.getAbsolutePath());
        } else {
            Sprite sprite = new Sprite(pictureFile);
            AssetPool.addSprite(pictureFile, sprite);
            return AssetPool.sprites.get(file.getAbsolutePath());
        }
    }

    public static Shader getShader(String shaderPath) {
        File file = new File(shaderPath);
        if (AssetPool.hasShader(shaderPath)) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(shaderPath);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Spritesheet getSpritesheet(String pictureFile) {
        File file = new File(pictureFile);
        if (AssetPool.hasSpritesheet(file.getAbsolutePath())) {
            return AssetPool.spritesheets.get(file.getAbsolutePath());
        } else {
            assert false : "Spritesheet '" + file.getAbsolutePath() + "' does not exist.";
        }
        return null;
    }

    public static void addSprite(String pictureFile, Sprite sprite) {
        File file = new File(pictureFile);
        if (!AssetPool.hasSprite(file.getAbsolutePath())) {
            AssetPool.sprites.put(file.getAbsolutePath(), sprite);
        } else {
            assert false : "Asset pool already has asset: " + file.getAbsolutePath();
        }
    }

    public static void addSpritesheet(String pictureFile, int tileWidth, int tileHeight,
                                      int spacing, int columns, int size) {
        File file = new File(pictureFile);
        if (!AssetPool.hasSpritesheet(file.getAbsolutePath())) {
            Spritesheet spritesheet = new Spritesheet(pictureFile, tileWidth, tileHeight,
                    spacing, columns, size);
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }
}
