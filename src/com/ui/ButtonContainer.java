package com.ui;

import com.component.SnapToGrid;
import com.component.Spritesheet;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.LevelEditorScene;
import com.jade.Window;
import com.util.Constants;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ButtonContainer extends Component {
    private List<GameObject> buttons;
    private int activeItem = -1;

    public ButtonContainer() {
        buttons = new ArrayList<>();
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/marioTilesheet.png");
        int totalSprites = 10 * 33;
        int current = 0;
        while (current < totalSprites) {
            for (int i = 0; i < 10; i++) {
                int y = Constants.TILES_OFFSET_Y + ((current / 10) * 16) + ((current / 10) * Constants.TILES_VERTICAL_PADDING);
                int x = Constants.TILES_OFFSET_X + (i * 16) + (i * Constants.TILES_HORIZONTAL_PADDING);
                GameObject tile = new GameObject("Tile", new Transform(new Vector2(x, y)), 0);
                tile.transform.scale.x = 2;
                tile.transform.scale.y = 2;
                tile.addComponent(spritesheet.sprites.get(current).copy());
                tile.addComponent(new Button(spritesheet.sprites.get(current), new Vector2(x, y), new Vector2(16, 16)));
                buttons.add(tile);
                current++;
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        for (GameObject go : buttons) { go.update(dt); }

        if (Button.ACTIVE_ITEM != activeItem && Button.ACTIVE_ITEM >= 0 && Button.ACTIVE_ITEM < buttons.size()) {
            activeItem = Button.ACTIVE_ITEM;
            LevelEditorScene scene = (LevelEditorScene)Window.getScene();
            SnapToGrid snapToGrid = scene.mouseCursor.getComponent(SnapToGrid.class);
            scene.mouseCursor = buttons.get(activeItem).copy();
            scene.mouseCursor.removeComponent(Button.class);
            scene.mouseCursor.addComponent(snapToGrid);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        for (GameObject go : buttons) { go.getComponent(Button.class).draw(g2); }
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
