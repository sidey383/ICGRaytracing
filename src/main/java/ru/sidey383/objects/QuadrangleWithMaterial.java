package ru.sidey383.objects;

import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.model.TriangleLineSupplier;
import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.raytrace.RaytraceObject;
import ru.sidey383.raytrace.TriangleRaytraceObject;

public record QuadrangleWithMaterial(QuadrangleDescription description, Vector3 diffuse, Vector3 specular, double power) implements DrawableObject {
    public QuadrangleWithMaterial {
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
    }

    @Override
    public LinesSupplier getLineSupplier() {
        return new TriangleLineSupplier(new TriangleDescription(description.a(), description.b(), description.c()))
                .composition(new TriangleLineSupplier(new TriangleDescription(description.a(), description.c(), description.d())));
    }

    @Override
    public RaytraceObject getRaytraceObject() {
        return new TriangleRaytraceObject(new TriangleDescription(description.a(), description.b(), description.c()), diffuse, specular, power)
                .composition(new TriangleRaytraceObject(new TriangleDescription(description.a(), description.c(), description.d()), diffuse, specular, power));
    }

    @Override
    public BoxDescription getBoxDescription() {
        Vector3 min = new Vector3Record(
                Math.min(Math.min(description.a().x(), description.b().x()), Math.min(description.c().x(), description.d().x())),
                Math.min(Math.min(description.a().y(), description.b().y()), Math.min(description.c().y(), description.d().y())),
                Math.min(Math.min(description.a().z(), description.b().z()), Math.min(description.c().z(), description.d().z()))
        );
        Vector3 max = new Vector3Record(
                Math.max(Math.max(description.a().x(), description.b().x()), Math.max(description.c().x(), description.d().x())),
                Math.max(Math.max(description.a().y(), description.b().y()), Math.max(description.c().y(), description.d().y())),
                Math.max(Math.max(description.a().z(), description.b().z()), Math.max(description.c().z(), description.d().z()))
        );
        return new BoxDescription(min, max);
    }
}
