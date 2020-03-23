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
    float timeLeft;
    private int currentSprite;
    private float width, height;
    private boolean loops;

    private Map<String, String> stateTransfers;
    private List<Float> waitTimes;
    private String animationName;
    public AnimationMachine machine;

    public Animation(String name, float speed, List<Sprite> sprites, boolean loops) {
        List<Float> speeds = new ArrayList<>();
        for (int i=0; i < sprites.size(); i++) {
            speeds.add(speed);
        }
        init(name, speeds, sprites, loops);
    }

    public Animation(String name, List<Float> speed, List<Sprite> sprites, boolean loops) {
        init(name, speed, sprites, loops);
    }

    public void init(String name, List<Float> speeds, List<Sprite> sprites, boolean loops) {
        this.animationName = name;
        this.sprites = new ArrayList<>();
        this.waitTimes = new ArrayList<>();
        this.waitTimes.addAll(speeds);
        for (Sprite sprite : sprites) {
            Sprite copy = (Sprite)sprite.copy();
            this.sprites.add(copy);
        }
        this.timeLeft = waitTimes.get(0);
        this.width = this.sprites.get(0).width;
        this.height = this.sprites.get(0).height;
        this.stateTransfers = new HashMap<>();
        this.loops = loops;
    }

    public Sprite getCurrentSprite() {
        return this.sprites.get(this.currentSprite);
    }

    @Override
    public void update(double dt) {
        this.timeLeft -= dt;

        if (this.timeLeft <= 0.0f) {
            if (loops) {
                this.currentSprite = (this.currentSprite + 1) % this.sprites.size();
            } else {
                this.currentSprite = Math.min(this.currentSprite + 1, this.sprites.size() - 1);
            }

            this.timeLeft = this.waitTimes.get(this.currentSprite);
        }
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
        Animation animation = new Animation(this.animationName, this.waitTimes, spriteCopies, this.loops);
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
        builder.append(addBooleanProperty("Loops", this.loops, tabSize + 1, true, true));
        builder.append(addIntProperty("NumberOfSprites", this.sprites.size(), tabSize + 1, true, true));
        for (int i=0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);
            builder.append(sprite.serialize(tabSize + 1));
            builder.append(addEnding(true, true));
        }
        builder.append(addIntProperty("WaitTimesSize", this.waitTimes.size(), tabSize + 1, true, true));
        for (int i=0; i < waitTimes.size(); i++) {
            builder.append(addFloatProperty("WaitTime", waitTimes.get(i), tabSize + 1, true, true));
        }

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
        boolean loops = Parser.consumeBooleanProperty("Loops");
        Parser.consume(',');

        int numOfSprites = Parser.consumeIntProperty("NumberOfSprites");
        Parser.consume(',');
        List<Sprite> sprites = new ArrayList<>();
        for (int i=0; i < numOfSprites; i++) {
            Sprite sprite = (Sprite)Parser.parseComponent();
            Parser.consume(',');
            sprites.add(sprite);
        }

        int numWaitTimes = Parser.consumeIntProperty("WaitTimesSize");
        Parser.consume(',');
        List<Float> waitTimes = new ArrayList<>();
        for (int i=0; i < numWaitTimes; i++) {
            waitTimes.add(Parser.consumeFloatProperty("WaitTime"));
            Parser.consume(',');
        }
        Animation anim = new Animation(name, waitTimes, sprites, loops);

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
