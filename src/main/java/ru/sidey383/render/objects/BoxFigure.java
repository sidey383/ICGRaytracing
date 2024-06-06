package ru.sidey383.render.objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.render.raytrace.IntersectionInfo;
import ru.sidey383.render.raytrace.Ray;

import java.util.List;
import java.util.Optional;

@Getter
@Accessors(fluent = true)
public final class BoxFigure implements Figure {
    private final Vector3 min;
    private final Vector3 max;
    private final Vector3 diffuse;
    private final Vector3 specular;
    private final double power;
    private final List<Pair<Vector>> lines;

    public BoxFigure(Vector3 min, Vector3 max, Vector3 diffuse, Vector3 specular, double power) {
        if (min.x() > max.x()) {
            double min0 = min().x();
            min = new Vector3(max.x(), min.y(), min.z());
            max = new Vector3(min0, max.y(), max.z());
        }
        if (min.y() > max.y()) {
            double min1 = min().y();
            min = new Vector3(min.x(), max.y(), min.z());
            max = new Vector3(max.x(), min1, max.z());
        }
        if (min.z() > max.z()) {
            double min2 = min().z();
            min = new Vector3(min.x(), min.y(), max.z());
            max = new Vector3(max.x(), max.y(), min2);
        }
        if (power < 0)
            throw new IllegalArgumentException("Power must be positive");
        this.min = min;
        this.max = max;
        this.diffuse = diffuse;
        this.specular = specular;
        this.power = power;
        Vector v_0_0_0 = min.toVector4();
        Vector v_1_0_0 = new Vector3(max.x(), min.y(), min.z()).toVector4();
        Vector v_0_1_0 = new Vector3(min.x(), max.y(), min.z()).toVector4();
        Vector v_1_1_0 = new Vector3(max.x(), max.y(), min.z()).toVector4();
        Vector v_0_0_1 = new Vector3(min.x(), min.y(), max.z()).toVector4();
        Vector v_1_0_1 = new Vector3(max.x(), min.y(), max.z()).toVector4();
        Vector v_0_1_1 = new Vector3(min.x(), max.y(), max.z()).toVector4();
        Vector v_1_1_1 = max.toVector4();
        lines = List.of(
                new Pair<>(v_0_0_0, v_1_0_0),
                new Pair<>(v_1_0_0, v_1_1_0),
                new Pair<>(v_1_1_0, v_0_1_0),
                new Pair<>(v_0_1_0, v_0_0_0),
                new Pair<>(v_0_0_1, v_1_0_1),
                new Pair<>(v_1_0_1, v_1_1_1),
                new Pair<>(v_1_1_1, v_0_1_1),
                new Pair<>(v_0_1_1, v_0_0_1),
                new Pair<>(v_0_0_0, v_0_0_1),
                new Pair<>(v_1_0_0, v_1_0_1),
                new Pair<>(v_1_1_0, v_1_1_1),
                new Pair<>(v_0_1_0, v_0_1_1)
        );
    }

    @Override
    public List<Pair<Vector>> getLines() {
        return lines;
    }

    @Override
    public Optional<IntersectionInfo> intersect(Ray ray) {
        double minDist = Double.MAX_VALUE;
        Vector3 normal = null;
        {
            for (int i = 0; i < 3; i++) {
                double dir = ray.direction().get(i);
                if (dir == 0)
                    continue;
                double dist1 = (min.get(i) - ray.origin().get(i)) / dir;
                double dist2 = (max.get(i) - ray.origin().get(i)) / dir;
                double dist = Math.min(dist1, dist2);
                if (dist >= minDist || dist <= 0)
                    continue;
                int cord1 = (i + 1) % 3;
                int cord2 = (i + 1) % 3;
                double val1 = ray.point(dist).get(cord1);
                double val2 = ray.point(dist).get(cord2);
                if (val1 >= min.get(cord1) && val1 <= max.get(cord1) && val2 >= min.get(cord2) && val2 <= max.get(cord2)) {
                    minDist = dist;
                    normal = switch (i) {
                        case 0 -> new Vector3(dir > 0 ? -1 : 1, 0, 0);
                        case 1 -> new Vector3(0, dir > 0 ? -1 : 1, 0);
                        case 2 -> new Vector3(0, 0, dir > 0 ? -1 : 1);
                        default -> throw new RuntimeException("Impossible");
                    };
                }

            }
        }
        if (normal == null)
            return Optional.empty();
        else
            return Optional.of(new IntersectionInfo(minDist, normal, diffuse, specular, power));
    }


}
