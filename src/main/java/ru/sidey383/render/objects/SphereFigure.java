package ru.sidey383.render.objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.render.linemodel.model.SphereLineSupplier;
import ru.sidey383.render.raytrace.IntersectionInfo;
import ru.sidey383.render.raytrace.Ray;

import java.util.List;

@Getter
@Accessors(fluent = true)
public final class SphereFigure implements Figure {
    private final Vector3 position;
    private final double radius;
    private final Vector3 diffuse;
    private final Vector3 specular;
    private final double power;
    private final SphereLineSupplier supplier;

    private final Vector3 min;

    private final Vector3 max;

    public SphereFigure(Vector3 position, double radius, Vector3 diffuse, Vector3 specular, double power) {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be greater than 0");
        this.position = position;
        this.radius = radius;
        this.diffuse = diffuse;
        this.specular = specular;
        this.power = power;
        this.supplier = new SphereLineSupplier(position, radius);
        this.min = new Vector3(position.x() - radius, position.y() - radius, position.z() - radius);
        this.max = new Vector3(position.x() + radius, position.y() + radius, position.z() + radius);
    }

    @Override
    public List<Pair<Vector>> getLines() {
        return supplier.getLines();
    }

    @Override
    public IntersectionInfo intersect(Ray ray) {
        Vector3 l = position.sub(ray.origin());
        double tca = l.dot(ray.direction());
        if (tca < 0)
            return null;
        double d2 = l.dot(l) - tca * tca;
        if (d2 > radius * radius)
            return null;
        double thc = Math.sqrt(radius * radius - d2);
        double t0 = tca - thc;
        double t1 = tca + thc;
        if (t0 > t1) {
            double tmp = t0;
            t0 = t1;
            t1 = tmp;
        }
        if (t0 < 0) {
            t0 = t1;
            if (t0 < 0)
                return null;
        }
        Vector3 hit = ray.origin().add(ray.direction().mul(t0));
        Vector3 normal = hit.sub(position).normalize();
        return new IntersectionInfo(t0, normal, diffuse, specular, power);
    }

}
