package com.file;

import com.component.*;
import com.jade.Component;
import com.jade.GameObject;
import com.ui.*;
import com.ui.buttons.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Parser {
    private static int offset = 0;
    private static int line = 1;
    private static byte[] bytes;

    public static void openLevelFile(String filename) {
        File tmp = new File("assets/levels/" + filename + ".zip");
        if (!tmp.exists()) {
            bytes = new byte[0];
            return;
        }
        offset = 0;
        line = 1;

        try {
            ZipFile zipFile = new ZipFile("assets/levels/" + filename + ".zip");
            ZipEntry jsonFile = zipFile.getEntry(filename + ".json");
            InputStream stream = zipFile.getInputStream(jsonFile);

            Parser.bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void openFile(String filename) {
        File tmp = new File(filename + ".zip");
        if (!tmp.exists()) {
            bytes = new byte[0];
            return;
        }
        offset = 0;
        line = 1;

        try {
            ZipFile zipFile = new ZipFile(filename + ".zip");
            ZipEntry jsonFile = zipFile.getEntry(filename + ".json");
            InputStream stream = zipFile.getInputStream(jsonFile);

            Parser.bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static GameObject parseGameObject() {
        if (bytes.length == 0 || atEnd()) return null;

        if (peek() == ',') Parser.consume(',');
        skipWhitespace();
        if (atEnd()) return null;

        return GameObject.deserialize();
    }

    public static JWindow parseJWindow() {
        if (bytes.length == 0 || atEnd()) return null;

        if (peek() == ',') Parser.consume(',');
        skipWhitespace();
        if (atEnd()) return null;

        return JWindow.deserialize();
    }

    public static void skipWhitespace() {
        while (!atEnd() && (peek() == ' ' || peek() == '\n' || peek() == '\t' || peek() == '\r')) {
            if (peek() == '\n') Parser.line++;
            advance();
        }
    }

    public static char peek() {
        return (char)bytes[offset];
    }

    public static char advance() {
        char c = (char)bytes[offset];
        offset++;
        return c;
    }

    public static void consume(char c) {
        char actual = peek();
        if (actual != c) {
            System.out.println("Error: Expected '" + c + "' but instead got '" + actual + "' at line: " + Parser.line);
            System.exit(-1);
        }
        offset++;
    }

    public static boolean atEnd() {
        return offset >= bytes.length;
    }

    public static int parseInt() {
        skipWhitespace();
        char c;
        StringBuilder builder = new StringBuilder();

        while(!atEnd() && isDigit(peek()) || peek() == '-') {
            c = advance();
            builder.append(c);
        }

        return Integer.parseInt(builder.toString());
    }

    public static double parseDouble() {
        skipWhitespace();
        char c;
        StringBuilder builder = new StringBuilder();

        while(!atEnd() && isDigit(peek()) || peek() == '-' || peek() == '.') {
            c = advance();
            builder.append(c);
        }

        return Double.parseDouble(builder.toString());
    }

    public static float parseFloat() {
        float f = (float)parseDouble();
        consume('f');
        return f;
    }

    public static String parseString() {
        skipWhitespace();
        char c;
        StringBuilder builder = new StringBuilder();
        consume('"');

        while (!atEnd() && peek() != '"') {
            c = advance();
            builder.append(c);
        }
        consume('"');

        return builder.toString();
    }

    public static boolean parseBoolean() {
        skipWhitespace();
        StringBuilder builder = new StringBuilder();

        if (!atEnd() && peek() == 't') {
            builder.append("true");
            consume('t');
            consume('r');
            consume('u');
            consume('e');
        } else if (!atEnd() && peek() == 'f') {
            builder.append("false");
            consume('f');
            consume('a');
            consume('l');
            consume('s');
            consume('e');
        } else {
            System.out.println("Expecting 'true' or 'false' instead got: " + peek() + " at line: " + Parser.line);
            System.exit(-1);
        }

        return builder.toString().compareTo("true") == 0;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static Component parseComponent() {
        String componentTitle = Parser.parseString();
        skipWhitespace();
        Parser.consume(':');
        skipWhitespace();
        Parser.consume('{');

        switch (componentTitle) {
            case "Sprite":
                return Sprite.deserialize();
            case "AnimationMachine":
                return AnimationMachine.deserialize();
            case "SpriteRenderer":
                return SpriteRenderer.deserialize();
            case "BoxBounds":
                return BoxBounds.deserialize();
            case "Rigidbody":
                return Rigidbody.deserialize();
            case "PlayerController":
                return PlayerController.deserialize();
            case "Brick":
                return Brick.deserialize();
//            case "BoxBounds":
//                return BoxBounds.deserialize();
//            case "TriangleBounds":
//                return TriangleBounds.deserialize();
            default:
                System.out.println("Could not find component '" + componentTitle + "' at line: " + Parser.line);
                System.exit(-1);
        }

        return null;
    }

    public static JComponent parseJComponent() {
        String componentTitle = Parser.parseString();
        skipWhitespace();
        Parser.consume(':');
        skipWhitespace();
        Parser.consume('{');

        switch (componentTitle) {
            case "Button":
                return Button.deserialize();
            case "FileExplorerButton":
                return FileExplorerButton.deserialize();
            case "SaveLevelButton":
                return SaveLevelButton.deserialize();
            case "ZIndexButton":
                return ZIndexButton.deserialize();
            case "LineBreak":
                return LineBreak.deserialize();
            case "NewLevelButton":
                return NewLevelButton.deserialize();
            case "TestLevelButton":
                return TestLevelButton.deserialize();
            default:
                System.out.println("Could not find component '" + componentTitle + "' at line: " + Parser.line);
                System.exit(-1);
        }

        return null;
    }

    public static String consumeStringProperty(String name) {
        skipWhitespace();
        checkString(name);
        consume(':');
        return parseString();
    }

    public static int consumeIntProperty(String name) {
        skipWhitespace();
        checkString(name);
        consume(':');
        return parseInt();
    }

    public static float consumeFloatProperty(String name) {
        skipWhitespace();
        checkString(name);
        consume(':');
        return parseFloat();
    }

    public static double consumeDoubleProperty(String name) {
        skipWhitespace();
        checkString(name);
        consume(':');
        return parseDouble();
    }

    public static boolean consumeBooleanProperty(String name) {
        skipWhitespace();
        checkString(name);
        consume(':');
        return parseBoolean();
    }

    public static void consumeEndObjectProperty() {
        skipWhitespace();
        consume('}');
    }

    public static void consumeBeginObjectProperty(String name) {
        skipWhitespace();
        checkString(name);
        skipWhitespace();
        consume(':');
        skipWhitespace();
        consume('{');
    }

    private static void checkString(String str) {
        String title = Parser.parseString();
        if (title.compareTo(str) != 0) {
            System.out.println("Expected '" + str + "' instead got '" + title + "' at line: " + Parser.line);
            System.exit(-1);
        }
    }
}
