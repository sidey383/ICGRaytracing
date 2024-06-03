package ru.sidey383.objects;

import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;

public record SphereDescription(Vector3 position, double radius) {
    public SphereDescription {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be greater than 0");
    }

}
