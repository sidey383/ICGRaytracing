package ru.sidey383.objects;

import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.model.TriangleLineSupplier;
import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.raytrace.RaytraceObject;
import ru.sidey383.raytrace.TriangleRaytraceObject;

public record TriangleWithMaterial(TriangleDescription description, Vector3 diffuse, Vector3 specular, double power) implements DrawableObject {
    public TriangleWithMaterial {
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
    }

    @Override
    public LinesSupplier getLineSupplier() {
        return new TriangleLineSupplier(description);
    }

    @Override
    public RaytraceObject getRaytraceObject() {
        return new TriangleRaytraceObject(description, diffuse, specular, power);
    }

    @Override
    public BoxDescription getBoxDescription() {
        Vector3 min = new Vector3Record(Math.min(Math.min(description.a().x(), description.b().x()), description.c().x()),
                Math.min(Math.min(description.a().y(), description.b().y()), description.c().y()),
                Math.min(Math.min(description.a().z(), description.b().z()), description.c().z()));
        Vector3 max = new Vector3Record(Math.max(Math.max(description.a().x(), description.b().x()), description.c().x()),
                Math.max(Math.max(description.a().y(), description.b().y()), description.c().y()),
                Math.max(Math.max(description.a().z(), description.b().z()), description.c().z()));
        return new BoxDescription(min, max);
    }
}
