package ru.sidey383.objects;

import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;

import java.util.List;

public record BoxDescription(Vector3 min, Vector3 max) {
    public BoxDescription {
        if (min.get(0) > max.get(0) || min.get(1) > max.get(1) || min.get(2) > max.get(2))
            throw new IllegalArgumentException("Min must be less than max");
    }

    public List<TriangleDescription> triangulation() {
        return List.of(
                new TriangleDescription(min, new Vector3Record(min.get(0), max.get(1), min.get(2)), new Vector3Record(max.get(0), max.get(1), min.get(2))),
                new TriangleDescription(min, new Vector3Record(max.get(0), max.get(1), min.get(2)), new Vector3Record(max.get(0), min.get(1), min.get(2))),
                new TriangleDescription(min, new Vector3Record(max.get(0), min.get(1), min.get(2)), new Vector3Record(min.get(0), min.get(1), min.get(2))),
                new TriangleDescription(max, new Vector3Record(min.get(0), min.get(1), max.get(2)), new Vector3Record(max.get(0), min.get(1), max.get(2))),
                new TriangleDescription(max, new Vector3Record(max.get(0), min.get(1), max.get(2)), new Vector3Record(max.get(0), max.get(1), max.get(2))),
                new TriangleDescription(max, new Vector3Record(max.get(0), max.get(1), max.get(2)), new Vector3Record(min.get(0), max.get(1), max.get(2)))
        );
    }

    public BoxDescription getBoundingBox() {
        return this;
    }

}
