package com.main;

import com.jade.Window;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Window window = Window.getWindow();
            window.run();
        } catch (Throwable e) {
            System.out.println("Oh no! It looks like an error has occured. Please copy the contents of error.log to https://github.com/codingminecraft/Mario/issues.");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("error.log"));
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                writer.write(sw.toString());
                writer.close();
            } catch (IOException e2) {
                System.out.println("A truly fatal error has occured. I cannot open a file to write the original application error. Please log this to the same website as above.");
                e2.printStackTrace();
            }

            e.printStackTrace();
        }
    }
}
