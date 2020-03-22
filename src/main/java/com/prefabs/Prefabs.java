package com.prefabs;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.jade.GameObject;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject MARIO_PREFAB() {
        GameObject player = new GameObject("Mario_Prefab", new Transform(new Vector2f()), 0);

        Spritesheet characterSprites = AssetPool.getSpritesheet("assets/character_and_enemies_32.png");

        AnimationMachine playerMachine = new AnimationMachine();
        Animation idle = new Animation("Idle", 0.2f, characterSprites.sprites.subList(0, 1), false);
        Animation run = new Animation("Run", 0.2f, characterSprites.sprites.subList(0, 4), true);
        Animation jump = new Animation("Jump", 0.2f, characterSprites.sprites.subList(4, 6), false);
        Animation swim = new Animation("Swim", 0.2f, characterSprites.sprites.subList(9, 14), true);
        playerMachine.setStartAnimation("Idle");

        // IDLE
        idle.addStateTransfer("StartRunning", "Run");
        idle.addStateTransfer("StartJumping", "Jump");
        idle.addStateTransfer("StartSwim", "Swim");

        // RUN
        run.addStateTransfer("StartJumping", "Jump");
        run.addStateTransfer("StartIdle", "Idle");
        run.addStateTransfer("StartSwim", "Swim");

        // JUMP
        jump.addStateTransfer("StartRunning", "Run");
        jump.addStateTransfer("StartIdle", "Idle");
        jump.addStateTransfer("StartSwim", "Swim");

        // SWIM
        swim.addStateTransfer("StartRunning", "Run");
        swim.addStateTransfer("StartJumping", "Jump");
        swim.addStateTransfer("StartIdle", "Idle");

        playerMachine.addAnimation(idle);
        playerMachine.addAnimation(run);
        playerMachine.addAnimation(jump);
        playerMachine.addAnimation(swim);

        player.addComponent(playerMachine);
        player.addComponent(new SpriteRenderer(playerMachine.getPreviewSprite()));

        BoxBounds playerBoxBounds = new BoxBounds(30, 32, false);
        playerBoxBounds.xBuffer = 1;
        player.addComponent(playerBoxBounds);

        player.addComponent(new Rigidbody());
        player.addComponent(new PlayerController());

        player.transform.scale.x = 32;
        player.transform.scale.y = 32;

        return player;
    }

    public static GameObject GOOMBA_PREFAB(int type) {
        GameObject goomba = new GameObject("Goomba_Prefab", new Transform(new Vector2f()), 0);

        Spritesheet characterSprites = AssetPool.getSpritesheet("assets/character_and_enemies_32.png");

        AnimationMachine goombaMachine = new AnimationMachine();
        Animation walk = null;
        Animation squash = null;
        goombaMachine.setStartAnimation("Walk");

        switch (type) {
            case 1:
                walk = new Animation("Walk", 0.2f, characterSprites.sprites.subList(17, 19), true);
                squash = new Animation("Squash", 0.2f, characterSprites.sprites.subList(19, 20), false);
                break;
            case 2:
                walk = new Animation("Walk", 0.2f, characterSprites.sprites.subList(20, 22), true);
                squash = new Animation("Squash", 0.2f, characterSprites.sprites.subList(22, 23), false);
                break;
            case 3:
                walk = new Animation("Walk", 0.2f, characterSprites.sprites.subList(23, 25), true);
                squash = new Animation("Squash", 0.2f, characterSprites.sprites.subList(25, 26), false);
                break;
            case 0:
            default:
                walk = new Animation("Walk", 0.2f, characterSprites.sprites.subList(14, 16), true);
                squash = new Animation("Squash", 0.2f, characterSprites.sprites.subList(16, 17), false);
                break;
        }

        // WALK
        walk.addStateTransfer("StartSquash", "Squash");

        // SQUASH
        squash.addStateTransfer("StartWalk", "Walk");

        goombaMachine.addAnimation(walk);
        goombaMachine.addAnimation(squash);

        goomba.addComponent(goombaMachine);

        goomba.addComponent(new SpriteRenderer(goombaMachine.getPreviewSprite()));
        goomba.transform.scale.x = 32;
        goomba.transform.scale.y = 32;

        goomba.addComponent(new BoxBounds(32, 32, false));
        goomba.addComponent(new Rigidbody());

        return goomba;
    }
}
