package com.renderer;

import com.jade.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private final int UI_MAX_BATCH_SIZE = 100;
    private final int LOW_UI_MAX_BATCH_SIZE = 150;
    private List<RenderBatch> batches;
    private List<UIRenderBatch> uiBatches;
    private List<UIRenderBatch> lowUIBatches;
    private Camera camera;

    public Renderer(Camera camera) {
        this.batches = new ArrayList<>();
        this.uiBatches = new ArrayList<>();
        this.lowUIBatches = new ArrayList<>();
        this.camera = camera;
    }

    public Camera camera() {
        return this.camera;
    }

    public void add(RenderComponent renderable) {
        boolean added = false;
        renderable.isDirty = true;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom && batch.zIndex == renderable.gameObject.zIndex) {
                if (renderable.getTexture() == null || (batch.hasTexture(renderable.getTexture()) || batch.hasTextureRoom())) {
                    batch.add(renderable);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, this, renderable.gameObject.zIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.add(renderable);

            Collections.sort(batches);
        }
    }

    public void addLow(UIRenderComponent renderable) {
        boolean added = false;
        for (UIRenderBatch batch : lowUIBatches) {
            if (batch.hasRoom && batch.zIndex == renderable.getZIndex()) {
                if (renderable.getTexture() == null || (batch.hasTexture(renderable.getTexture()) || batch.hasTextureRoom())) {
                    batch.add(renderable);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            UIRenderBatch newBatch = new UIRenderBatch(LOW_UI_MAX_BATCH_SIZE, this, renderable.getZIndex());
            newBatch.start();
            lowUIBatches.add(newBatch);
            newBatch.add(renderable);

            Collections.sort(lowUIBatches);
        }
    }

    public void add(UIRenderComponent renderable) {
        boolean added = false;
        for (UIRenderBatch batch : uiBatches) {
            if (batch.hasRoom && batch.zIndex == renderable.getZIndex()) {
                if (renderable.getTexture() == null || (batch.hasTexture(renderable.getTexture()) || batch.hasTextureRoom())) {
                    batch.add(renderable);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            UIRenderBatch newBatch = new UIRenderBatch(UI_MAX_BATCH_SIZE, this, renderable.getZIndex());
            newBatch.start();
            uiBatches.add(newBatch);
            newBatch.add(renderable);

            Collections.sort(uiBatches);
        }
    }

    public void render() {
        for (UIRenderBatch batch : lowUIBatches) {
            batch.render();
        }

        for (RenderBatch batch : batches) {
            batch.render();
        }

        for (UIRenderBatch uiBatch : uiBatches) {
            uiBatch.render();
        }
    }
}
