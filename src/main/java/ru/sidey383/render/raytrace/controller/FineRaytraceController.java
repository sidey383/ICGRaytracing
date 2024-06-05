package ru.sidey383.render.raytrace.controller;

import ru.sidey383.render.camera.FinalCamera;
import ru.sidey383.math.Vector3;

import java.awt.image.BufferedImage;

public class FineRaytraceController extends AbstractRaytraceController {

    private final int force;

    public FineRaytraceController(int width, int height, FinalCamera camera, int force) {
        super(width * force, height * force, camera);
        this.force = force;
    }

    @Override
    public void apply(BufferedImage image, double gamma) {
        if (image.getHeight() * force != totalY || image.getWidth() * force != totalX)
            throw new IllegalArgumentException("Image size must be equal to the size of the controller");
        synchronized (this) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    double rf = 0;
                    double gf = 0;
                    double bf = 0;
                    int count = 0;
                    for (int fy = 0; fy < force; fy++) {
                        for (int fx = 0; fx < force; fx++) {
                            Vector3 color = this.colors[y * force + fy][x * force + fx];
                            if (color != null) {
                                rf += Math.pow(color.get(0) / maxColor, gamma) * 255;
                                gf += Math.pow(color.get(1) / maxColor, gamma) * 255;
                                bf += Math.pow(color.get(2) / maxColor, gamma) * 255;
                                count++;
                            }
                        }
                    }
                    int r = (int) (rf / count);
                    int g = (int) (gf / count);
                    int b = (int) (bf / count);
                    int rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
                    image.setRGB(x, y, rgb);
                }
            }
        }
    }
}
