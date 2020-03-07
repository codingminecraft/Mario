package com.component;

import com.file.Parser;
import com.jade.Component;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class AnimationMachine extends Component {
    List<Animation> animations;
    Animation current;

    public AnimationMachine() {
        this.animations = new ArrayList<>();
        this.current = null;
    }

    public void addAnimation(Animation animation) {
        animation.machine = this;
        this.animations.add(animation);
    }

    public Animation getAnimation(String name) {
        for (Animation anim : animations) {
            if (anim.is(name)) {
                return anim;
            }
        }

        return null;
    }

    @Override
    public void start() {
        this.current = this.animations.get(0);
    }

    @Override
    public void update(double dt) {
        current.update(dt);
    }

    public void trigger(String trigger) {
        current = current.trigger(trigger);
    }

    public Sprite getPreviewSprite() {
        return this.animations.get(0).getPreviewSprite();
    }

    @Override
    public Component copy() {
        AnimationMachine machine = new AnimationMachine();
        for (Animation anim : animations) {
            machine.addAnimation((Animation)anim.copy());
        }
        machine.start();

        return machine;
    }

    @Override
    public void draw(Graphics2D g2) {
        current.draw(g2);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("AnimationMachine", tabSize));
        builder.append(addIntProperty("NumberOfAnimations", animations.size(), tabSize + 1, true, true));
        for (int i=0; i < animations.size(); i++) {
            Animation anim = animations.get(i);
            builder.append(anim.serialize(tabSize + 1));
            builder.append(addEnding(true, true));
        }

        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static AnimationMachine deserialize() {
        AnimationMachine machine = new AnimationMachine();

        int numOfAnimations = Parser.consumeIntProperty("NumberOfAnimations");
        Parser.consume(',');
        for (int i=0; i < numOfAnimations; i++) {
            Parser.consumeBeginObjectProperty("Animation");
            Animation anim = Animation.deserialize();
            machine.addAnimation(anim);
            Parser.consume(',');
        }

        Parser.consumeEndObjectProperty();

        return machine;
    }
}
