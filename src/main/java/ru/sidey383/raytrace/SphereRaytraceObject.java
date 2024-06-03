package ru.sidey383.raytrace;

import ru.sidey383.math.Vector3;
import ru.sidey383.objects.SphereDescription;

import java.util.Optional;

public record SphereRaytraceObject(Vector3 position, double radius, Vector3 diffuse,
                                   Vector3 specular, double power) implements RaytraceObject {

    public SphereRaytraceObject {
        if (radius < 0)
            throw new IllegalArgumentException("Radius must be positive");
    }

    public SphereRaytraceObject(SphereDescription description, Vector3 diffuse, Vector3 specular, double power) {
        this(description.position(), description.radius(), diffuse, specular, power);
    }

    @Override
    public Optional<IntersectionInfo> intersect(Ray ray) {
        Vector3 l = position.sub(ray.origin());
        double tca = l.dot(ray.direction());
        if (tca < 0)
            return Optional.empty();
        double d2 = l.dot(l) - tca * tca;
        if (d2 > radius * radius)
            return Optional.empty();
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
                return Optional.empty();
        }
        Vector3 normal = ray.origin().add(ray.direction().mul(t0)).sub(position).normalize();
        return Optional.of(new IntersectionInfo(t0, normal, diffuse, specular, power));
    }

}
