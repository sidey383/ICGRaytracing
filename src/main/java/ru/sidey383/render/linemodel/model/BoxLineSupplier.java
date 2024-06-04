package ru.sidey383.render.linemodel.model;

import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.render.objects.BoxFigure;

import java.util.List;

public record BoxLineSupplier (BoxFigure boxFigure) implements LinesSupplier {
    @Override
    public List<Pair<Vector>> getLines() {
        Vector v_0_0_0 = boxFigure.min().toVector4();
        Vector v_1_0_0 = new Vector3Record(boxFigure.max().get(0), boxFigure.min().get(1), boxFigure.min().get(2)).toVector4();
        Vector v_0_1_0 = new Vector3Record(boxFigure.min().get(0), boxFigure.max().get(1), boxFigure.min().get(2)).toVector4();
        Vector v_1_1_0 = new Vector3Record(boxFigure.max().get(0), boxFigure.max().get(1), boxFigure.min().get(2)).toVector4();
        Vector v_0_0_1 = new Vector3Record(boxFigure.min().get(0), boxFigure.min().get(1), boxFigure.max().get(2)).toVector4();
        Vector v_1_0_1 = new Vector3Record(boxFigure.max().get(0), boxFigure.min().get(1), boxFigure.max().get(2)).toVector4();
        Vector v_0_1_1 = new Vector3Record(boxFigure.min().get(0), boxFigure.max().get(1), boxFigure.max().get(2)).toVector4();
        Vector v_1_1_1 = boxFigure.max().toVector4();
        return List.of(
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
}
