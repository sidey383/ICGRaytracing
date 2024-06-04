package ru.sidey383.render.raytrace;

import ru.sidey383.math.Vector3;

public record IntersectionInfo(double distance, Vector3 normal, Vector3 diffuse, Vector3 specular, double power) {
    public IntersectionInfo {
        if (distance < 0)
            throw new IllegalArgumentException("Distance must be positive");
    }

}
