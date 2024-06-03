package ru.sidey383.raytrace;

import ru.sidey383.math.Vector3;

public record Ray(Vector3 origin, Vector3 direction) {
    public Ray {
        direction = direction.normalize();
    }

    public Vector3 point(double distance) {
        return origin.add(direction.mul(distance));
    }

}
