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
public class TriangleFigure implements Figure {
    private final Vector3 a;
    private final Vector3 b;
    private final Vector3 c;
    private final Vector3 diffuse;
    private final Vector3 specular;
    private final double power;
    private final Vector3 normal;
    private final List<Pair<Vector>> lines;
    private final Vector3 min;
    private final Vector3 max;


    public TriangleFigure(Vector3 a, Vector3 b, Vector3 c, Vector3 diffuse, Vector3 specular, double power) {
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
        this.a = a;
        this.b = b;
        this.c = c;
        this.diffuse = diffuse;
        this.specular = specular;
        this.max = new Vector3(
                Math.max(Math.max(a.x(), b.x()), c.x()),
                Math.max(Math.max(a.y(), b.y()), c.y()),
                Math.max(Math.max(a.z(), b.z()), c.z())
        );
        this.min = new Vector3(
                Math.min(Math.min(a.x(), b.x()), c.x()),
                Math.min(Math.min(a.y(), b.y()), c.y()),
                Math.min(Math.min(a.z(), b.z()), c.z())
        );
        this.power = power;
        this.normal = CalculationUtils.calculateNormal(a, b, c);
        lines = List.of(
                new Pair<>(a.toVector4(), b.toVector4()),
                new Pair<>(b.toVector4(), c.toVector4()),
                new Pair<>(c.toVector4(), a.toVector4())
        );
    }


    @Override
    public List<Pair<Vector>> getLines() {
        return lines;
    }

    @Override
    public Optional<IntersectionInfo> intersect(Ray ray) {
        Double d = CalculationUtils.triangleOneSideIntersect(ray, a, b, c, normal);
        if (d == null)
            return Optional.empty();
        else
            return Optional.of(new IntersectionInfo(d, normal, diffuse, specular, power));
    }
}
