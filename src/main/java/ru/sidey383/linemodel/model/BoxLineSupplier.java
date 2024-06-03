package ru.sidey383.linemodel.model;

import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.objects.BoxDescription;

import java.util.List;

public record BoxLineSupplier (BoxDescription boxDescription) implements LinesSupplier {
    @Override
    public List<Pair<Vector>> createLines() {
        Vector v_0_0_0 = boxDescription.min().toVector4();
        Vector v_1_0_0 = new Vector3Record(boxDescription.max().get(0), boxDescription.min().get(1), boxDescription.min().get(2)).toVector4();
        Vector v_0_1_0 = new Vector3Record(boxDescription.min().get(0), boxDescription.max().get(1), boxDescription.min().get(2)).toVector4();
        Vector v_1_1_0 = new Vector3Record(boxDescription.max().get(0), boxDescription.max().get(1), boxDescription.min().get(2)).toVector4();
        Vector v_0_0_1 = new Vector3Record(boxDescription.min().get(0), boxDescription.min().get(1), boxDescription.max().get(2)).toVector4();
        Vector v_1_0_1 = new Vector3Record(boxDescription.max().get(0), boxDescription.min().get(1), boxDescription.max().get(2)).toVector4();
        Vector v_0_1_1 = new Vector3Record(boxDescription.min().get(0), boxDescription.max().get(1), boxDescription.max().get(2)).toVector4();
        Vector v_1_1_1 = boxDescription.max().toVector4();
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
