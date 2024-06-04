package ru.sidey383;

import ru.sidey383.camera.FinalCamera;
import ru.sidey383.configuration.Quality;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.render.linemodel.paint.PerspectiveLinesPainter;
import ru.sidey383.render.objects.DrawableObject;
import ru.sidey383.render.raytrace.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        BufferedImage roughImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        BufferedImage fineImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        BufferedImage normalImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        RenderConfiguration renderConfiguration = RenderConfiguration.parseConfiguration(ConfigurationUtility.readFile(Path.of("sample.render")));
        SceneConfiguration sceneConfiguration = SceneConfiguration.readConfiguration(ConfigurationUtility.readFile(Path.of("sample.scene")));
        FinalCamera camera = new FinalCamera(renderConfiguration);
        PerspectiveLinesPainter painter = new PerspectiveLinesPainter(camera);
        painter.createImage(
                sceneConfiguration.objects(),
                image.createGraphics(),
                image.getWidth(),
                image.getHeight()
        );
        try (OutputStream os = Files.newOutputStream(Path.of("result.png"))) {
            ImageIO.write(image, "png", os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RaytraceConfiguration fineConfiguration = new RaytraceConfiguration(
                image.getWidth(),
                image.getHeight(),
                camera,
                Quality.FINE,
                sceneConfiguration.objects(),
                sceneConfiguration.lights(),
                renderConfiguration.traceDepth(),
                sceneConfiguration.ambient(),
                renderConfiguration.background()
        );
        RaytraceConfiguration roughConfiguration = new RaytraceConfiguration(
                image.getWidth(),
                image.getHeight(),
                camera,
                Quality.ROUGH,
                sceneConfiguration.objects(),
                sceneConfiguration.lights(),
                renderConfiguration.traceDepth(),
                sceneConfiguration.ambient(),
                renderConfiguration.background()
        );
        RaytraceConfiguration normalConfiguration = new RaytraceConfiguration(
                image.getWidth(),
                image.getHeight(),
                camera,
                Quality.NORMAL,
                sceneConfiguration.objects(),
                sceneConfiguration.lights(),
                renderConfiguration.traceDepth(),
                sceneConfiguration.ambient(),
                renderConfiguration.background()
        );
        ImageRaytracing fine = new ImageRaytracing(fineConfiguration, 4);
        fine.accept(() -> {
            fine.drawImage(fineImage, 1);
            try (OutputStream os = Files.newOutputStream(Path.of("resultFine.png"))) {
                ImageIO.write(fineImage, "png", os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ImageRaytracing rough = new ImageRaytracing(roughConfiguration, 4);
        rough.accept(() -> {
            rough.drawImage(roughImage, 1);
            try (OutputStream os = Files.newOutputStream(Path.of("resultRough.png"))) {
                ImageIO.write(roughImage, "png", os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ImageRaytracing normal = new ImageRaytracing(normalConfiguration, 4);
        normal.accept(() -> {
            normal.drawImage(normalImage, 1);
            try (OutputStream os = Files.newOutputStream(Path.of("resultNormal.png"))) {
                ImageIO.write(normalImage, "png", os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (!(normal.isComplete() || fine.isComplete() || rough.isComplete())) {
            try {
                System.out.println("Normal: " + normal.completeCount() + "/" + normal.totalCount());
                System.out.println("Fine: " + fine.completeCount() + "/" + fine.totalCount());
                System.out.println("Rough: " + rough.completeCount() + "/" + rough.totalCount());
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
