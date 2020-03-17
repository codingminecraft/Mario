package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Util {
    public static ByteBuffer fileToByteBuffer(String filepath) {
        File fontFile = new File(filepath);
        ByteBuffer fontBuffer = null;

        try {
            InputStream is = new FileInputStream(fontFile);
            fontBuffer = ByteBuffer.wrap(is.readAllBytes());
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return fontBuffer;
    }
}
