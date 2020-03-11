package com.renderer;

import com.jade.Camera;
import com.jade.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 10000;
    private List<RenderBatch> batches;
    private RenderBatch currentBatch;
    private Camera camera;

    private int currentCount = 0;

    public Renderer(Camera camera) {
        this.batches = new ArrayList<>();
        this.currentBatch = new RenderBatch(MAX_BATCH_SIZE, this);
        this.batches.add(currentBatch);
        this.camera = camera;
    }

    public void start() {
        for (RenderBatch batch : batches) {
            batch.start();
        }
    }

    public Camera camera() {
        return this.camera;
    }

    public void add(RenderComponent renderable) {
        currentCount++;
        if (currentCount >= MAX_BATCH_SIZE) {
            this.currentBatch = new RenderBatch(MAX_BATCH_SIZE, this);
            this.batches.add(currentBatch);
            currentCount = 0;
        }
        this.currentBatch.add(renderable);
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
