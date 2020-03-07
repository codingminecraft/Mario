package com.component;

import com.file.Parser;
import com.jade.Component;
import org.w3c.dom.html.HTMLImageElement;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation extends Component {
    private List<Sprite> sprites;
    private float speed;
    float timeLeft;
    private int currentSprite;
    private float width, height;

    private Map<String, String> stateTransfers;
    private String animationName;
    public AnimationMachine machine;

    public Animation(String name, float speed, List<Sprite> sprites) {
        this.animationName = name;
        this.sprites = new ArrayList<>();
        for (Sprite sprite : sprites) {
            this.sprites.add((Sprite)sprite.copy());
        }
        this.speed = speed;
        this.timeLeft = speed;
        this.width = this.sprites.get(0).width;
        this.height = this.sprites.get(0).height;
        this.stateTransfers = new HashMap<>();
    }

    @Override
    public void update(double dt) {
        this.timeLeft -= dt;

        if (this.timeLeft <= 0.0f) {
            this.currentSprite = (this.currentSprite + 1) % this.sprites.size();
            this.timeLeft = this.speed;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(this.sprites.get(this.currentSprite).image,
                (int)machine.gameObject.transform.position.x, (int)machine.gameObject.transform.position.y,
                (int)(this.width * machine.gameObject.transform.scale.x), (int)(this.height * machine.gameObject.transform.scale.y), null);
    }

    public Animation trigger(String trigger) {
        if (machine.getAnimation(stateTransfers.get(trigger)) != null)
            return machine.getAnimation(stateTransfers.get(trigger));
        return this;
    }

    public void addStateTransfer(String trigger, String animationName) {
        stateTransfers.putIfAbsent(trigger, animationName);
    }

    public boolean is(String name) {
        return this.animationName.equals(name);
    }

    public Sprite getPreviewSprite() {
        return sprites.get(0);
    }

    @Override
    public Component copy() {
        List<Sprite> spriteCopies = new ArrayList<>();
        for (Sprite sprite : this.sprites) {
            spriteCopies.add((Sprite)sprite.copy());
        }
        Animation animation = new Animation(this.animationName, this.speed, spriteCopies);
        for (String key : stateTransfers.keySet()) {
            animation.addStateTransfer(key, stateTransfers.get(key));
        }

        return animation;
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("Animation", tabSize));

        builder.append(addStringProperty("Name", this.animationName, tabSize + 1, true, true));
        builder.append(addIntProperty("NumberOfSprites", this.sprites.size(), tabSize + 1, true, true));
        for (int i=0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);
            builder.append(sprite.serialize(tabSize + 1));
            builder.append(addEnding(true, true));
        }
        builder.append(addFloatProperty("Speed", this.speed, tabSize + 1, true, true));
        builder.append(addIntProperty("NumberOfStates", this.stateTransfers.size(), tabSize + 1,true, true));
        for (String state : stateTransfers.keySet()) {
            builder.append(addStringProperty("Trigger", state, tabSize + 1, true, true));
            builder.append(addStringProperty("State", stateTransfers.get(state), tabSize + 1, true, true));
        }
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Animation deserialize() {
        String name = Parser.consumeStringProperty("Name");
        Parser.consume(',');

        int numOfSprites = Parser.consumeIntProperty("NumberOfSprites");
        Parser.consume(',');
        List<Sprite> sprites = new ArrayList<>();
        for (int i=0; i < numOfSprites; i++) {
            Sprite sprite = (Sprite)Parser.parseComponent();
            Parser.consume(',');
            sprites.add(sprite);
        }

        float speed = Parser.consumeFloatProperty("Speed");
        Parser.consume(',');
        Animation anim = new Animation(name, speed, sprites);

        int numOfStates = Parser.consumeIntProperty("NumberOfStates");
        Parser.consume(',');
        for (int i=0; i < numOfStates; i++) {
            String trigger = Parser.consumeStringProperty("Trigger");
            Parser.consume(',');
            String state = Parser.consumeStringProperty("State");
            Parser.consume(',');
            anim.addStateTransfer(trigger, state);
        }

        Parser.consumeEndObjectProperty();

        return anim;
    }
}