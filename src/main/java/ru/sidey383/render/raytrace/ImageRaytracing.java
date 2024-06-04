package ru.sidey383.render.raytrace;

import lombok.Getter;
import ru.sidey383.camera.Camera;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.raytrace.controller.FineRaytraceController;
import ru.sidey383.render.raytrace.controller.NormalRaytraceController;
import ru.sidey383.render.raytrace.controller.RaytraceController;
import ru.sidey383.render.raytrace.controller.RoughRaytraceController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ImageRaytracing implements Consumer<Runnable> {

    private ScheduledExecutorService executor;
    private Thread controllerThread;
    private RaytraceController controller;
    private final int threadCount;
    private final RaytraceConfiguration configuration;
    private final AtomicBoolean complete = new AtomicBoolean(false);

    public ImageRaytracing(RaytraceConfiguration configuration, int threadCount) {
        this.threadCount = threadCount;
        this.configuration = configuration;
    }

    @Override
    public void accept(Runnable onComplete) {
        if (!isComplete())
            throw new IllegalStateException("Raytracing is already running");
        AtomicBoolean localComplete = new AtomicBoolean(false);
        Runnable localOnComplete = () -> {
            if (localComplete.get())
                return;
            localComplete.set(true);
            complete.set(true);
            onComplete.run();
            executor.shutdownNow();
        };
        controller = switch (configuration.quality()) {
            case FINE -> new FineRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2, localOnComplete);
            case ROUGH -> new RoughRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2, localOnComplete);
            case NORMAL -> new NormalRaytraceController(configuration.width(), configuration.height(), configuration.camera(), localOnComplete);
        };
        executor = new ScheduledThreadPoolExecutor(threadCount);
        complete.set(false);
        controllerThread = new Thread(() -> {
            List<Future<?>> futures = new ArrayList<>(controller.total());
            for (int y = 0; y < controller.totalY(); y++) {
                for (int x = 0; x < controller.totalX(); x++) {
                    futures.add(executor.submit(new RayTraceTask(x, y, controller, configuration)));
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
            localOnComplete.run();
        });
        controllerThread.start();
    }

    public int completeCount() {
        if (controller == null)
            return 0;
        return controller.complete();
    }

    public int totalCount() {
        if (controller == null)
            return -1;
        return controller.total();
    }

    public boolean isComplete() {
        if (controller == null)
            return true;
        return controller.complete() == controller.total();
    }

    public void shutdown() {
        if (controllerThread != null)
            controllerThread.interrupt();
        if (executor != null)
            executor.shutdownNow();
    }

    public void drawImage(BufferedImage image, double gamma) {
        if (controller == null)
            return;
        controller.apply(image, gamma);
    }

}
