package ru.sidey383.render.raytrace;

import org.jetbrains.annotations.NotNull;
import ru.sidey383.render.raytrace.controller.FineRaytraceController;
import ru.sidey383.render.raytrace.controller.NormalRaytraceController;
import ru.sidey383.render.raytrace.controller.RaytraceController;
import ru.sidey383.render.raytrace.controller.RoughRaytraceController;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RaytracingRender {

    public record RenderStatus(
            int completeCount,
            int totalCount,
            boolean isComplete,
            boolean isRunning,
            boolean isFailed,
            Throwable error
    ) {
    }

    @NotNull
    private final AtomicReference<Thread> controllerThread = new AtomicReference<>(null);
    @NotNull
    private final RaytraceController controller;
    private final int threadCount;
    @NotNull
    private final RaytraceConfiguration configuration;
    @NotNull
    private final AtomicBoolean isComplete = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    @NotNull
    private final AtomicReference<Throwable> error = new AtomicReference<>();

    public RaytracingRender(RaytraceConfiguration configuration, int threadCount) {
        this.threadCount = threadCount;
        this.configuration = configuration;
        controller = switch (configuration.quality()) {
            case FINE ->
                    new FineRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2);
            case ROUGH ->
                    new RoughRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2);
            case NORMAL ->
                    new NormalRaytraceController(configuration.width(), configuration.height(), configuration.camera());
        };
    }

    private synchronized void catchException(Thread t, Throwable e) {
        if (error.get() != null)
            return;
        error.set(e);
    }

    public void startRender(Consumer<RenderStatus> onComplete) {
        Thread monitor = new Thread(() -> {
            synchronized (this) {
                if (isRunning.get()) {
                    onComplete.accept(getStatus());
                    return;
                }
            }
            try {
                isRunning.set(true);
                List<RayTraceThread> threads = new ArrayList<>(this.threadCount);
                AtomicInteger counter = new AtomicInteger(0);
                for (int i = 0; i < this.threadCount; i++) {
                    threads.add(new RayTraceThread(controller, configuration, counter));
                }
                threads.forEach(t -> t.setUncaughtExceptionHandler(this::catchException));
                threads.forEach(Thread::start);
                boolean isCompleteThreads = true;
                for (RayTraceThread t : threads) {
                    try {
                        t.join();
                        isCompleteThreads = isCompleteThreads && t.isComplete();
                    } catch (InterruptedException e) {
                        if (error.get() == null)
                            error.set(e);
                        for (RayTraceThread ti : threads) {
                            ti.interrupt();
                        }
                    }
                }
                isComplete.set(true);
            } catch (Throwable t) {
                error.set(t);
            } finally {
                isRunning.set(false);
                onComplete.accept(getStatus());
            }
        });
        if (!controllerThread.compareAndSet(null, monitor))
            onComplete.accept(getStatus());
        else
            monitor.start();
    }

    public RenderStatus getStatus() {
        Throwable e = error.get();
        return new RenderStatus(
                controller.completePart(),
                controller.total(),
                isComplete.get(),
                isRunning.get(),
                e != null,
                e
        );
    }

    public void shutdown() {
        Thread t = controllerThread.get();
        if (t != null && !t.isInterrupted())
            t.interrupt();
    }

    public void drawImage(BufferedImage image, double gamma) {
        controller.apply(image, gamma);
    }

}
