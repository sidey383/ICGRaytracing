package ru.sidey383.objects;

import ru.sidey383.linemodel.model.BoxLineSupplier;
import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.math.Vector3;
import ru.sidey383.raytrace.RaytraceObject;
import ru.sidey383.raytrace.TriangleRaytraceObject;

import java.util.List;

public record BoxWithMaterial(BoxDescription box, Vector3 diffuse, Vector3 specular, double power) implements DrawableObject {
    public BoxWithMaterial {
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
    }

    @Override
    public LinesSupplier getLineSupplier() {
        return new BoxLineSupplier(box);
    }

    @Override
    public RaytraceObject getRaytraceObject() {
        List<TriangleDescription> triangles = box.triangulation();
        RaytraceObject o = null;
        for (TriangleDescription t : triangles) {
            if (o == null)
                o = new TriangleRaytraceObject(t, diffuse, specular, power);
            else
                o = o.composition(new TriangleRaytraceObject(t, diffuse, specular, power));
        }
        return o;
    }

    @Override
    public BoxDescription getBoxDescription() {
        return box;
    }
}
