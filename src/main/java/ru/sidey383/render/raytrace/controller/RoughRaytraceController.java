package ru.sidey383.render.raytrace.controller;

import ru.sidey383.render.camera.FinalCamera;
import ru.sidey383.math.Vector3;

import java.awt.image.BufferedImage;

public class RoughRaytraceController extends AbstractRaytraceController {

    private final int force;

    public RoughRaytraceController(int width, int height, FinalCamera camera, int force) {
        super((width + force - 1) / force, (height + force - 1) / force, camera);
        this.force = force;
    }

    @Override
    public void apply(BufferedImage image, double gamma) {
        if ((image.getHeight() + force - 1) / force != totalY || (image.getWidth() + force - 1) / force != totalX)
            throw new IllegalArgumentException("Image size must be equal to the size of the controller");
        synchronized (this) {
            for (int y = 0; y < totalY; y++) {
                for (int x = 0; x < totalX; x++) {
                    Vector3 color = colors[y][x];
                    if (color == null)
                        continue;
                    int r = (int) (Math.pow(color.get(0) / maxColor, gamma) * 255);
                    int g = (int) (Math.pow(color.get(1) / maxColor, gamma) * 255);
                    int b = (int) (Math.pow(color.get(2) / maxColor, gamma) * 255);
                    int rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
                    for (int fy = 0; fy < force; fy++) {
                        for (int fx = 0; fx < force; fx++) {
                            if (fy + y * force < image.getHeight() && fx + x * force < image.getWidth())
                                image.setRGB(fx + x * force, fy + y * force, rgb);
                        }
                    }
                }
            }
        }
    }
}
