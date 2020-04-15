package com.main;

import com.jade.Window;

import java.io.*;

public class Main {
    // Method to compare two versions. Returns 1 if v2 is
    // smaller, -1 if v1 is smaller, 0 if equal
    static int versionCompare(String v1, String v2)
    {
        // vnum stores each numeric part of version
        int vnum1 = 0, vnum2 = 0;

        // loop untill both String are processed
        for (int i = 0, j = 0; (i < v1.length() || j < v2.length()); )
        {
            // storing numeric part of version 1 in vnum1
            while (i < v1.length() && v1.charAt(i) != '.')
            {
                vnum1 = vnum1 * 10 + (v1.charAt(i) - '0');
                i++;
            }

            // storing numeric part of version 2 in vnum2
            while (j < v2.length() && v2.charAt(j) != '.')
            {
                vnum2 = vnum2 * 10 + (v2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;

            // if equal, reset variables and go for next numeric
            // part
            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
    }

    public static void main(String[] args) {
        String[] customArgs = {
                "-Dorg.lwjgl.util.Debug=true",
                "-Djava.library.path=windows_Test/x64/org/lwjgl/;windows_Test/x64/org/lwjgl/glfw;windows_Test/x64/org/lwjgl/nfd;windows_Test/x64/org/lwjgl/openal;windows_Test/x64/org/lwjgl/stb;windows_Test/x64/org/lwjgl/opengl"
        };
        trueMain(customArgs);
    }

    public static void trueMain(String[] args) {
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
