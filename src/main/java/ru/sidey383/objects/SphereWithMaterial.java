package ru.sidey383.objects;

import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.model.SphereLineSupplier;
import ru.sidey383.math.Vector3;
import ru.sidey383.raytrace.RaytraceObject;
import ru.sidey383.raytrace.SphereRaytraceObject;

public record SphereWithMaterial(SphereDescription description, Vector3 diffuse, Vector3 specular, double power) implements DrawableObject {
    public SphereWithMaterial {
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
    }

    @Override
    public LinesSupplier getLineSupplier() {
        return new SphereLineSupplier(description);
    }

    @Override
    public RaytraceObject getRaytraceObject() {
        return new SphereRaytraceObject(description, diffuse, specular, power);
    }

    @Override
    public BoxDescription getBoxDescription() {
        return new BoxDescription(description.position().sub(description.radius()), description.position().add(description.radius()));
    }
}
