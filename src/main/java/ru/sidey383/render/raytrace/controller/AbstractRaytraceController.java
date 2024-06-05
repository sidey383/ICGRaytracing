package ru.sidey383.render.raytrace.controller;

import ru.sidey383.render.camera.FinalCamera;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.raytrace.Ray;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRaytraceController implements RaytraceController {

    private final FinalCamera camera;
    private final AtomicInteger completeCount;
    private final Vector3 viewFlatness;
    private final double width;
    private final double height;
    protected final int totalX;
    protected final int totalY;
    /**
     * Not thread safe
     **/
    protected final Vector3[][] colors;
    protected double maxColor = 0;
    protected final Object sync = new Object();

    public AbstractRaytraceController(int x, int y, FinalCamera camera) {
        this.camera = camera;
        colors = new Vector3[y][x];
        this.totalX = x;
        this.totalY = y;
        viewFlatness = camera.dir().mul(camera.near());
        this.height = camera.height();
        this.width = x * camera.height() / y;
        completeCount = new AtomicInteger();
    }

    @Override
    public Ray getRay(int x, int y) {
        return new Ray(
                camera.eye(),
                viewFlatness
                        .add(camera.right().mul(width * x / totalX - width / 2))
                        .add(camera.up().mul(-(height * y / totalY - height / 2)))

        );
    }

    @Override
    public void applyRay(Vector3 color, int x, int y) {
        synchronized (sync) {
            if (color.x() > maxColor) maxColor = color.x();
            if (color.y() > maxColor) maxColor = color.y();
            if (color.z() > maxColor) maxColor = color.z();
        }
        if (colors[y][x] != null)
            throw new IllegalStateException("Point already colored");
        colors[y][x] = color;
        completeCount.incrementAndGet();
    }

    public abstract void apply(BufferedImage image, double gamma);

    @Override
    public int completePart() {
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
