package ru.sidey383.render.objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.math.CalculationUtils;
import ru.sidey383.render.raytrace.IntersectionInfo;
import ru.sidey383.render.raytrace.Ray;

import java.util.List;
import java.util.Optional;

@Getter
@Accessors(fluent = true)
public class QuadrangleFigure implements Figure {

    private final Vector3 a;
    private final Vector3 b;
    private final Vector3 c;
    private final Vector3 d;
    private final Vector3 diffuse;
    private final Vector3 specular;
    private final double power;
    private final Vector3 min;
    private final Vector3 max;
    private final List<Pair<Vector>> lines;
    private final Vector3 firstNormal;
    private final Vector3 secondNormal;

    public QuadrangleFigure(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Vector3 diffuse, Vector3 specular, double power) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.diffuse = diffuse;
        this.specular = specular;
        this.power = power;
        this.min = new Vector3(
                Math.min(Math.min(a.x(), b.x()), Math.min(c.x(), d.x())),
                Math.min(Math.min(a.y(), b.y()), Math.min(c.y(), d.y())),
                Math.min(Math.min(a.z(), b.z()), Math.min(c.z(), d.z()))
        );
        this.max = new Vector3(
                Math.max(Math.max(a.x(), b.x()), Math.max(c.x(), d.x())),
                Math.max(Math.max(a.y(), b.y()), Math.max(c.y(), d.y())),
                Math.max(Math.max(a.z(), b.z()), Math.max(c.z(), d.z()))
        );
        Vector a4 = a.toVector4();
        Vector b4 = b.toVector4();
        Vector c4 = c.toVector4();
        Vector d4 = d.toVector4();
        lines = List.of(
                new Pair<>(a4, b4),
                new Pair<>(b4, c4),
                new Pair<>(c4, d4),
                new Pair<>(d4, a4)
        );
        firstNormal = CalculationUtils.calculateNormal(a, b, c);
        secondNormal = CalculationUtils.calculateNormal(a, c, d);
    }


    @Override
    public List<Pair<Vector>> getLines() {
        return lines;
    }

    @Override
    public Vector3 min() {
        return min;
    }

    @Override
    public Vector3 max() {
        return max;
    }

    @Override
    public Optional<IntersectionInfo> intersect(Ray ray) {
        Double dist1 = CalculationUtils.triangleIntersect(ray, a, b, c, firstNormal);
        Double dist2 = CalculationUtils.triangleIntersect(ray, a, c, d, secondNormal);
        final double dist;
        final Vector3 normal;
        if (dist1 != null) {
            if (dist2 != null) {
                if (Math.abs(dist1) < Math.abs(dist2)) {
                    if (dist1 < 0) {
                        dist = -dist1;
                        normal = firstNormal.mul(-1);
                    } else {
                        dist = dist1;
                        normal = firstNormal;
                    }
                } else {
                    if (dist2 < 0) {
                        dist = -dist2;
                        normal = firstNormal.mul(-1);
                    } else {
                        dist = dist2;
                        normal = firstNormal;
                    }
                }
            } else {
                if (dist1 < 0) {
                    dist = -dist1;
                    normal = firstNormal.mul(-1);
                } else {
                    dist = dist1;
                    normal = firstNormal;
                }
            }
        } else {
            if (dist2 != null) {
                if (dist2 < 0) {
                    dist = -dist2;
                    normal = firstNormal.mul(-1);
                } else {
                    dist = dist2;
                    normal = firstNormal;
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(new IntersectionInfo(dist, normal, diffuse, specular, power));
    }

}
