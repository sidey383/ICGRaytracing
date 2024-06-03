package ru.sidey383.raytrace;

import lombok.Getter;
import ru.sidey383.camera.Camera;
import ru.sidey383.math.Vector3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ImageRaytracing implements Consumer<Runnable> {

    @Getter
    private BufferedImage image;

    private Vector3[][] colors;

    private ScheduledExecutorService executor;

    private Thread workThread;

    private final int threadCount;

    private final RaytraceConfiguration configuration;

    private final AtomicInteger completeCount = new AtomicInteger(0);

    private final AtomicBoolean complete = new AtomicBoolean(true);

    public ImageRaytracing(BufferedImage image, RaytraceConfiguration configuration, int threadCount) {
        this.image = image;
        this.threadCount = threadCount;
        this.configuration = configuration;
    }

    @Override
    public void accept(Runnable onComplete) {
        if (!isComplete())
            throw new IllegalStateException("Raytracing is already running");
        completeCount.set(0);
        complete.set(false);
        executor = new ScheduledThreadPoolExecutor(threadCount);
        colors = new Vector3[image.getWidth()][image.getHeight()];
        workThread = new Thread(() -> {
            List<Future<?>> futures = new ArrayList<>(this.image.getHeight() * this.image.getWidth());
            for (int y = 0; y < this.image.getHeight(); y++) {
                for (int x = 0; x < this.image.getWidth(); x++) {
                    int finalX = x;
                    int finalY = y;
                    Ray ray = getRay(configuration.camera(), finalX, finalY, this.image.getHeight(), this.image.getWidth());
                    RayTraceTask task = new RayTraceTask(
                            ray, configuration.depth(), configuration.objectList(), configuration.lights(), configuration.ambient(),
                            (color) -> {
                                synchronized (this) {
                                    colors[finalX][finalY] = color;
                                }
                                completeCount.incrementAndGet();
                            }
                    );
                    futures.add(executor.submit(task));
                }
            }
            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
                if (completeCount() == totalCount())
                    break;
            }
            onComplete.run();
            executor.shutdown();
            complete.set(true);
        });
        workThread.start();
    }

    public int completeCount() {
        return completeCount.get();
    }

    public int totalCount() {
        return image.getHeight() * image.getWidth();
    }

    public boolean isComplete() {
        if (executor == null)
            return true;
        return complete.get();
    }

    public void shutdown() {
        if (workThread != null)
            workThread.interrupt();
        if (executor != null)
            executor.shutdownNow();
    }

    public BufferedImage calculateImage() {
        synchronized (this) {
            if (colors == null)
                return image;
            double max = 1e-9;
            for (Vector3[] row : colors) {
                for (Vector3 color : row) {
                    if (color != null) {
                        max = Math.max(max, color.get(0));
                        max = Math.max(max, color.get(1));
                        max = Math.max(max, color.get(2));
                    }
                }
            }
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Vector3 color = colors[x][y];
                    if (color != null) {
                        int r = (int) (color.get(0) * 255 / max);
                        int g = (int) (color.get(1) * 255 / max);
                        int b = (int) (color.get(2) * 255 / max);
                        image.setRGB(x, y, new Color(r, g, b).getRGB());
                    }
                }
            }
        }
        return image;
    }

    private static Ray getRay(Camera camera, int x, int y, int height, int width) {
        Vector3 direction =
                camera.z().mul(camera.near())
                .add(camera.right().mul((x - (double)width / 2) * camera.height() / width))
                .add(camera.up().mul((y - (double)height / 2) * camera.height() / height));
         return new Ray(camera.pos(), direction);
    }

}
