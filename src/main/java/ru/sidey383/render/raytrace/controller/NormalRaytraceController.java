package ru.sidey383.render.raytrace.controller;

import ru.sidey383.render.camera.FinalCamera;
import ru.sidey383.math.Vector3;

import java.awt.image.BufferedImage;

public class NormalRaytraceController extends AbstractRaytraceController {
    public NormalRaytraceController(int width, int height, FinalCamera camera) {
        super(width, height, camera);
    }

    @Override
    public void apply(BufferedImage image, double gamma) {
        if (image.getHeight() != totalY || image.getWidth() != totalX)
            throw new IllegalArgumentException("Image size must be equal to the size of the controller");
        synchronized (this) {
            for (int y = 0; y < totalY; y++) {
                for (int x = 0; x < totalX; x++) {
                    Vector3 color = colors[y][x];
                    if (color == null)
                        continue;
                    int r = (int) (Math.pow(color.x() / maxColor, gamma) * 255);
                    int g = (int) (Math.pow(color.y() / maxColor, gamma) * 255);
                    int b = (int) (Math.pow(color.z() / maxColor, gamma) * 255);
                    int rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
                    image.setRGB(x, image.getHeight() - y - 1, rgb);
                }
            }
        }
    }
}
