package com.util;

public class Time {
    public static double timeStarted = System.nanoTime();

    public static float getTime() { return (float)((System.nanoTime() - timeStarted) * 1E-9); }
}
