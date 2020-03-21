package com.component;

import com.dataStructure.Transform;
import com.file.Parser;
import com.jade.*;
import org.joml.Vector2f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class AnimationMachine extends Component {
    List<Animation> animations;
    Animation current;
    String startAnimation;

    private boolean inLevelEditor = false;
    private GameObject levelEditorGo;
    private SpriteRenderer spriteRenderer;

    public AnimationMachine() {
        this.animations = new ArrayList<>();
        this.current = null;
    }

    public void setStartAnimation(String animation) {
        this.startAnimation = animation;
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
        Animation startAnim = this.getAnimation(startAnimation);
        assert startAnim != null : "Error: Start Animation was never set for: " + this.gameObject.getName() + ". Did you forget to setStartAnimation?";
        this.current = startAnim;

        Scene scene = Window.getScene();
        if (scene instanceof LevelEditorScene) {
            inLevelEditor = true;
            this.levelEditorGo = new GameObject("LevelEditorCopy", gameObject.transform.copy(), gameObject.zIndex);
            levelEditorGo.addComponent(getPreviewSprite().copy());
        }

        this.spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        assert this.spriteRenderer != null : "Animation machine must be attached to GameObject with SpriteRenderer!";
    }

    @Override
    public void update(double dt) {
        if (inLevelEditor) {
            Transform.copyValues(gameObject.transform, levelEditorGo.transform);
            levelEditorGo.transform.position.sub(Window.getScene().camera.position());
            levelEditorGo.zIndex = gameObject.zIndex;
        } else {
            spriteRenderer.sprite = current.getCurrentSprite();
            current.update(dt);
        }
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
        machine.setStartAnimation(this.startAnimation);

        return machine;
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("AnimationMachine", tabSize));
        builder.append(addStringProperty("StartAnimation", startAnimation, tabSize + 1, true, true));
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

        String startAnimation = Parser.consumeStringProperty("StartAnimation");
        machine.setStartAnimation(startAnimation);
        Parser.consume(',');
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
