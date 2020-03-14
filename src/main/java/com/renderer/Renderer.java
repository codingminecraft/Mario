package com.renderer;

import com.jade.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private final int UI_MAX_BATCH_SIZE = 50;
    private List<RenderBatch> batches;
    private List<UIRenderBatch> uiBatches;
    private Camera camera;

    public Renderer(Camera camera) {
        this.batches = new ArrayList<>();
        this.uiBatches = new ArrayList<>();
        this.camera = camera;
    }

    public void start() {
        for (RenderBatch batch : batches) {
            batch.start();
        }

        for (UIRenderBatch batch : uiBatches) {
            batch.start();
        }

        batches.sort(Collections.reverseOrder());
        uiBatches.sort(Collections.reverseOrder());
}

    public Camera camera() {
        return this.camera;
    }

    public void add(RenderComponent renderable) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom && batch.zIndex == renderable.gameObject.zIndex) {
                batch.add(renderable);
                added = true;
                break;
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, this, renderable.gameObject.zIndex);
            batches.add(newBatch);
            newBatch.add(renderable);
        }
    }

    public void add(UIRenderComponent renderable) {
        boolean added = false;
        for (UIRenderBatch batch : uiBatches) {
            if (batch.hasRoom && batch.zIndex == renderable.getZIndex()) {
                System.out.println("Added here with z-index: " + batch.zIndex);
                batch.add(renderable);
                added = true;
                break;
            }
        }
        if (!added) {
            UIRenderBatch newBatch = new UIRenderBatch(UI_MAX_BATCH_SIZE, this, renderable.getZIndex());
            System.out.println("Created new batch with z-index: " + renderable.getZIndex());
            uiBatches.add(newBatch);
            newBatch.add(renderable);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }

        for (UIRenderBatch uiBatch : uiBatches) {
            uiBatch.render();
        }
    }
}
