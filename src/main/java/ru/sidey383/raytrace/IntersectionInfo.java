package ru.sidey383.raytrace;

import ru.sidey383.math.Vector3;

public record IntersectionInfo(double distance, Vector3 normal, Vector3 diffuse, Vector3 specular, double power) {
    public IntersectionInfo {
        if (distance < 0)
            throw new IllegalArgumentException("Distance must be positive");
    }

    Vector3 calculatePosition(Vector3 start, Vector3 direction) {
        return start.add(direction.mul(distance));
    }

}
