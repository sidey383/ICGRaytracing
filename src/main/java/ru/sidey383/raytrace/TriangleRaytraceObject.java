package ru.sidey383.raytrace;

import ru.sidey383.math.Vector3;
import ru.sidey383.objects.TriangleDescription;

import java.util.Optional;

public record TriangleRaytraceObject(Vector3 a, Vector3 b, Vector3 c, Vector3 normal, Vector3 diffuse, Vector3 specular, double power, boolean isTwoSide) implements RaytraceObject {

    public TriangleRaytraceObject(Vector3 a, Vector3 b, Vector3 c, Vector3 normal, Vector3 diffuse, Vector3 specular, double power) {
        this(a, b, c, normal, diffuse, specular, power, false);
    }

    public TriangleRaytraceObject {
        normal = normal.normalize();
    }

    public TriangleRaytraceObject(TriangleDescription triangleDescription, Vector3 diffuse, Vector3 specular, double power) {
        this(triangleDescription.a(), triangleDescription.b(), triangleDescription.c(), triangleDescription.getNormal(), diffuse, specular, power);
    }

    public TriangleRaytraceObject(TriangleDescription triangleDescription, Vector3 diffuse, Vector3 specular, double power, boolean isTwoSide) {
        this(triangleDescription.a(), triangleDescription.b(), triangleDescription.c(), triangleDescription.getNormal(), diffuse, specular, power, isTwoSide);
    }

    @Override
    public Optional<IntersectionInfo> intersect(Ray ray) {
        Vector3 normal = this.normal();
        double k = normal.dot(ray.direction());
        if (k > 0) {
            if (isTwoSide) {
                normal = normal.mul(-1);
            } else {
                return Optional.empty();
            }
        }
        if (Math.abs(k) < 1e-10) {
            return Optional.empty();
        }
        double dist = this.normal().dot(a().sub(ray.origin())) / k;
        if (dist < 0)
            return Optional.empty();
        Vector3 planePoint = ray.point(dist);
        if (pointInTriangle(planePoint, ray.origin(), a(), b(), c()))
            return Optional.of(new IntersectionInfo(dist, normal, diffuse, specular, power));
        else
            return Optional.empty();
    }

    private static boolean pointInTriangle(Vector3 point, Vector3 p_0, Vector3 p_1, Vector3 p_2, Vector3 p_3) {
        double fullVolume = Math.abs(calculateVolume(p_0, p_1, p_2, p_3));
        if (fullVolume < 0.0001) {
            return false;
        }
        double p_0_volume = Math.abs(calculateVolume(point, p_1, p_2, p_3)) / fullVolume;
        double p_1_volume = Math.abs(calculateVolume(p_0, point, p_2, p_3)) / fullVolume;
        double p_2_volume = Math.abs(calculateVolume(p_0, p_1, point, p_3)) / fullVolume;
        double p_3_volume = Math.abs(calculateVolume(p_0, p_1, p_2, point)) / fullVolume;
        return !(p_0_volume + p_1_volume + p_2_volume + p_3_volume > 1.00000001);
    }

    private static double calculateVolume(Vector3 p_0, Vector3 p_1, Vector3 p_2, Vector3 p_3) {
        Vector3 first = p_1.sub(p_0);
        Vector3 second = p_2.sub(p_0);
        Vector3 third = p_3.sub(p_0);
        return tripleProduct(first, second, third);
    }

    private static double tripleProduct(Vector3 a, Vector3 b, Vector3 c) {
        return a.dot(b.cross(c));
    }


}
