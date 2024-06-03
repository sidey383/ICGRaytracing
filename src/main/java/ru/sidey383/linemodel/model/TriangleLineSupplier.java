package ru.sidey383.linemodel.model;

import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3;
import ru.sidey383.objects.TriangleDescription;

import java.util.List;

public record TriangleLineSupplier(Pair<Vector3> a, Pair<Vector3> b, Pair<Vector3> c) implements LinesSupplier {
    public TriangleLineSupplier(TriangleDescription triangle) {
        this(new Pair<>(triangle.a(), triangle.b()), new Pair<>(triangle.b(), triangle.c()), new Pair<>(triangle.c(), triangle.a()));
    }

    @Override
    public List<Pair<Vector>> createLines() {
        return List.of(
                new Pair<>(a.first().toVector4(), a.second().toVector4()),
                new Pair<>(b.first().toVector4(), b.second().toVector4()),
                new Pair<>(c.first().toVector4(), c.second().toVector4())
        );
    }
}
