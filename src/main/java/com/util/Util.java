package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Util {
    public static byte[] combine(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static ByteBuffer fileToByteBuffer(String filepath) {
        File fontFile = new File(filepath);
        ByteBuffer fontBuffer = null;

        try {
            InputStream is = new FileInputStream(fontFile);

            // Read input stream into a byte array
            byte[] finalBytes = new byte[0];
            while (is.available() != 0) {
                byte[] byteBuffer = new byte[is.available()];
                is.read(byteBuffer);
                finalBytes = Util.combine(finalBytes, byteBuffer);
            }
            fontBuffer = ByteBuffer.wrap(finalBytes);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return fontBuffer;
    }
}
