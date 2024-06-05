package ru.sidey383;

import ru.sidey383.model.ApplicationParameters;
import ru.sidey383.render.raytrace.RaytraceConfiguration;
import ru.sidey383.render.raytrace.RaytracingRender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ThreadTest {

    public static void main(String[] args) {
        final int width = 1000;
        final int height = 1000;
        ApplicationParameters parameters = new ApplicationParameters();
        RaytracingRender render = new RaytracingRender(
                new RaytraceConfiguration(
                        width,
                        height,
                        parameters.getCamera().toFinalCamera(),
                        parameters.getRaytraceSettings().getQuality(),
                        parameters.getSceneState().getObjects(),
                        parameters.getSceneState().getLight(),
                        0,
                        parameters.getRaytraceSettings().getAmbient(),
                        parameters.getRaytraceSettings().getBackground()
                ), 32
        );
        render.startRender((s) -> {
            System.out.println(s);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            render.drawImage(image, 1);
            try {
                ImageIO.write(image, "png", new java.io.File("render.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
