package com.prefabs;

import com.component.*;
import com.component.bricks.BreakableBrick;
import com.component.bricks.Brick;
import com.component.bricks.QuestionBlock;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.jade.GameObject;
import com.util.Constants;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prefabs {
    public static GameObject MARIO_PREFAB() {
        GameObject player = new GameObject("Mario_Prefab", new Transform(new Vector2f()), 0);

        Spritesheet characterSprites = AssetPool.getSpritesheet("assets/spritesheets/character_and_enemies_32.png");

        AnimationMachine playerMachine = new AnimationMachine();
        Animation idle = new Animation("Idle", 0.1f, characterSprites.sprites.subList(0, 1), false);
        Animation run = new Animation("Run", 0.1f, characterSprites.sprites.subList(0, 4), true);
        Animation jump = new Animation("Jump", 0.1f, characterSprites.sprites.subList(4, 6), false);
        Animation swim = new Animation("Swim", 0.1f, characterSprites.sprites.subList(9, 14), true);
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

        Spritesheet characterSprites = AssetPool.getSpritesheet("assets/spritesheets/character_and_enemies_32.png");

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
        goomba.addComponent(new GoombaAI());

        return goomba;
    }

    public static GameObject QUESTION_BLOCK() {
        Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");

        // Set up animation machine
        AnimationMachine questionBlockMachine = new AnimationMachine();

        List<Float> waitTimeIdle = new ArrayList<>(Arrays.asList(0.4f, 0.2f, 0.2f));
        Animation idle = new Animation("Idle", waitTimeIdle, items.sprites.subList(0, 3), true);
        Animation blockHit = new Animation("BlockHit", 0.1f, items.sprites.subList(3, 4), false);

        idle.addStateTransfer("HitBlock", "BlockHit");

        questionBlockMachine.addAnimation(idle);
        questionBlockMachine.addAnimation(blockHit);
        questionBlockMachine.setStartAnimation("Idle");

        // Create game object and add components
        GameObject questionBlock = new GameObject("Question_Block_Prefab", new Transform(new Vector2f()), 0);

        questionBlock.addComponent(questionBlockMachine);
        questionBlock.addComponent(new SpriteRenderer(questionBlockMachine.getPreviewSprite()));

        questionBlock.addComponent(new QuestionBlock());
        questionBlock.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, true));

        questionBlock.transform.scale.x = 32;
        questionBlock.transform.scale.y = 32;

        return questionBlock;
    }

    public static GameObject BRICK_BLOCK() {
        Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");

        GameObject brickBlock = new GameObject("Brick_Block_Prefab", new Transform(new Vector2f()), 0);
        brickBlock.addComponent(new BreakableBrick());
        brickBlock.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, true));
        brickBlock.addComponent(new SpriteRenderer(items.sprites.get(5)));

        brickBlock.transform.scale.x = 32;
        brickBlock.transform.scale.y = 32;

        return brickBlock;
    }

    public static GameObject COIN() {
        Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");

        // Set up animation machine
        AnimationMachine machine = new AnimationMachine();

        List<Float> waitTimes = new ArrayList<>(Arrays.asList(0.4f, 0.2f, 0.2f));
        Animation idle = new Animation("Idle", waitTimes, items.sprites.subList(7, 10), true);
        Animation collect = new Animation("Collect", 0.1f, items.sprites.subList(7, 8), false);

        idle.addStateTransfer("CollectCoin", "Collect");

        machine.addAnimation(idle);
        machine.addAnimation(collect);
        machine.setStartAnimation("Idle");

        // Add components
        GameObject coin = new GameObject("Coin_Prefab", new Transform(new Vector2f()), 0);
        coin.addComponent(machine);

        coin.addComponent(new Coin());
        coin.addComponent(new BoxBounds(32, 32, true));
        coin.addComponent(new SpriteRenderer(machine.getPreviewSprite()));

        coin.transform.scale.x = 32;
        coin.transform.scale.y = 32;

        return coin;
    }
}
