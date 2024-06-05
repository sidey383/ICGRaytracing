package ru.sidey383.render.raytrace.controller;

import ru.sidey383.math.Vector3;
import ru.sidey383.render.raytrace.Ray;

import java.awt.image.BufferedImage;

public interface RaytraceController {

    Ray getRay(int x, int y);

    void applyRay(Vector3 color, int x, int y);

    int completePart();

    int total();

    int totalX();

    int totalY();

    void apply(BufferedImage image, double gamma);

}
