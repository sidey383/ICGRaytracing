package ru.sidey383;

import ru.sidey383.camera.FinalCamera;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.linemodel.model.SphereLineSupplier;
import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.model.TriangleLineSupplier;
import ru.sidey383.linemodel.paint.PerspectiveLinesPainter;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.objects.DrawableObject;
import ru.sidey383.raytrace.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class App 
{
    public static void main( String[] args ) throws IOException {
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        RenderConfiguration configuration = RenderConfiguration.parseConfiguration(ConfigurationUtility.readFile(Path.of("sample.render")));
        SceneConfiguration sceneConfiguration = SceneConfiguration.readConfiguration(ConfigurationUtility.readFile(Path.of("sample.scene")));
        FinalCamera camera = new FinalCamera(configuration);
        PerspectiveLinesPainter painter = new PerspectiveLinesPainter(camera);
        painter.createImage(
                sceneConfiguration.objects().stream().map(DrawableObject::getLineSupplier).collect(Collectors.toList()),
                image.createGraphics(),
                image.getWidth(),
                image.getHeight()
        );
        try (OutputStream os = Files.newOutputStream(Path.of("result1.png"))) {
            ImageIO.write(image, "png", os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RaytraceConfiguration raytraceConfiguration = new RaytraceConfiguration(
                camera,
                sceneConfiguration.objects().stream().map(DrawableObject::getRaytraceObject).collect(Collectors.toList()),
                sceneConfiguration.lights(),
                configuration.traceDepth(),
                sceneConfiguration.ambient()
        );
        ImageRaytracing raytracing = new ImageRaytracing(image, raytraceConfiguration, 16);
        raytracing.accept(() -> {
            BufferedImage cimage = raytracing.calculateImage();
            try (OutputStream os = Files.newOutputStream(Path.of("result2.png"))) {
                ImageIO.write(cimage, "png", os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (!raytracing.isComplete()) {
            try {
                System.out.println(raytracing.completeCount() + " / " + raytracing.totalCount());
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
