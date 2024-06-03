package ru.sidey383.objects;

import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;

public record TriangleDescription (Vector3 a, Vector3 b, Vector3 c) {

    public Vector3 getNormal() {
        Vector3 ab = b.sub(a);
        Vector3 ac = c.sub(a);
        return ab.cross(ac).normalize();
    }

    public BoxDescription getBoundingBox() {
        return new BoxDescription(
            new Vector3Record(
                Math.min(a.get(0), Math.min(b.get(0), c.get(0))),
                Math.min(a.get(1), Math.min(b.get(1), c.get(1))),
                Math.min(a.get(2), Math.min(b.get(2), c.get(2)))
            ),
            new Vector3Record(
                Math.max(a.get(0), Math.max(b.get(0), c.get(0))),
                Math.max(a.get(1), Math.max(b.get(1), c.get(1))),
                Math.max(a.get(2), Math.max(b.get(2), c.get(2)))
            )
        );
    }

}
