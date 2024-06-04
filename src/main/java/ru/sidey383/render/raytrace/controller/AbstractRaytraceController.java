package ru.sidey383.render.raytrace.controller;

import ru.sidey383.camera.FinalCamera;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.raytrace.Ray;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRaytraceController implements RaytraceController {

    private final FinalCamera camera;
    private final AtomicInteger completeCount = new AtomicInteger(0);
    private final Vector3 viewFlatness;
    private final double width;
    private final double height;
    private final Runnable onComplete;
    protected final int totalX;
    protected final int totalY;
    /**
     * Not thread safe
     * **/
    protected final Vector3[][] colors;
    protected double maxColor = 0;
    protected final Object sync = new Object();

    public AbstractRaytraceController(int x, int y, FinalCamera camera, Runnable onComplete) {
        this.onComplete = onComplete;
        this.camera = camera;
        colors = new Vector3[y][x];
        this.totalX = x;
        this.totalY = y;
        viewFlatness = camera.z().mul(camera.near());
        this.height = camera.height();
        this.width = x * camera.height() / y;
    }

    @Override
    public Ray getRay(int x, int y) {
        return new Ray(
                camera.pos(),
                viewFlatness
                        .add(camera.right().mul(width * x / totalX - width / 2))
                        .add(camera.up().mul(height * y / totalY - height / 2))

        );
    }

    @Override
    public void applyRay(Vector3 color, int x, int y) {
        synchronized (sync) {
            if (color.x() > maxColor) maxColor = color.x();
            if (color.y() > maxColor) maxColor = color.y();
            if (color.z() > maxColor) maxColor = color.z();
        }
        colors[y][x] = color;
        if (completeCount.incrementAndGet() == totalX * totalY)
            onComplete.run();
    }

    public abstract void apply(BufferedImage image, double gamma);

    @Override
    public int complete() {
        return completeCount.get();
    }

    @Override
    public int total() {
        return totalX * totalY;
    }

    @Override
    public int totalX() {
        return totalX;
    }

    @Override
    public int totalY() {
        return totalY;
    }
}
