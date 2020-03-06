package com.jade;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WL extends WindowAdapter {
    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        Scene scene = Window.getScene();
        if (scene instanceof LevelEditorScene) {
            LevelEditorScene levelEditorScene = (LevelEditorScene)scene;
            levelEditorScene.exportLevelEditorData();
        }
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
