package ru.sidey383.math;

import ru.sidey383.render.raytrace.Ray;

public class CalculationUtils {

    private CalculationUtils() {}

    public static Vector3 calculateNormal(Vector3 a, Vector3 b, Vector3 c) {
        Vector3 ab = b.sub(a);
        Vector3 ac = c.sub(a);
        return ab.cross(ac).normalize();
    }

    public static boolean pointInTriangle(Vector3 point, Vector3 p_0, Vector3 p_1, Vector3 p_2, Vector3 p_3) {
        double fullVolume = Math.abs(calculateVolume(p_0, p_1, p_2, p_3));
        if (fullVolume < 1e-9) {
            return false;
        }
        double p_0_volume = Math.abs(calculateVolume(point, p_1, p_2, p_3)) / fullVolume;
        double p_1_volume = Math.abs(calculateVolume(p_0, point, p_2, p_3)) / fullVolume;
        double p_2_volume = Math.abs(calculateVolume(p_0, p_1, point, p_3)) / fullVolume;
        double p_3_volume = Math.abs(calculateVolume(p_0, p_1, p_2, point)) / fullVolume;
        return p_0_volume + p_1_volume + p_2_volume + p_3_volume <= 1.00000001;
    }

    public static double calculateVolume(Vector3 p_0, Vector3 p_1, Vector3 p_2, Vector3 p_3) {
        Vector3 first = p_1.sub(p_0);
        Vector3 second = p_2.sub(p_0);
        Vector3 third = p_3.sub(p_0);
        return tripleProduct(first, second, third);
    }

    public static double tripleProduct(Vector3 a, Vector3 b, Vector3 c) {
        return a.dot(b.cross(c));
    }

    /**
     * @return distance to intersection point or null if there is no intersection distance will positive if ray is directed to the triangle
     * **/
    public static Double triangleIntersect(Ray ray, Vector3 a, Vector3 b, Vector3 c, Vector3 normal) {
        double invert = 1;
        double k = normal.dot(ray.direction());
        if (k > 0) {
            invert = -1;
        }
        if (Math.abs(k) < 1e-10) {
            return null;
        }
        double dist = normal.dot(a.sub(ray.origin())) / k;
        if (dist < 0)
            return null;
        Vector3 planePoint = ray.point(dist);
        if (CalculationUtils.pointInTriangle(planePoint, ray.origin(), a, b, c))
            return dist * invert;
        else
            return null;
    }

    public static Double triangleOneSideIntersect(Ray ray, Vector3 a, Vector3 b, Vector3 c, Vector3 normal) {
        double k = normal.dot(ray.direction());
        if (k > 0) {
            return null;
        }
        if (Math.abs(k) < 1e-10) {
            return null;
        }
        double dist = normal.dot(a.sub(ray.origin())) / k;
        if (dist < 0)
            return null;
        Vector3 planePoint = ray.point(dist);
        if (CalculationUtils.pointInTriangle(planePoint, ray.origin(), a, b, c))
            return dist;
        else
            return null;
    }

}
