package ru.sidey383.linemodel.model;

import ru.sidey383.math.Matrix;
import ru.sidey383.math.Vector;

import java.util.ArrayList;
import java.util.List;

public interface LinesSupplier {

    /**
     * @return list of pairs
     * **/
    default List<Pair<Vector>> createLines(Matrix transformation) {
        List<Pair<Vector>> vectorLine = createLines();
        return vectorLine.stream().map(pair -> pair.apply(transformation::multiply)).toList();
    }

    List<Pair<Vector>> createLines();

    default LinesSupplier composition(LinesSupplier supplier) {
        return () -> {
            List<Pair<Vector>> lines = new ArrayList<>();
            lines.addAll(createLines());
            lines.addAll(supplier.createLines());
            return lines;
        };
    }

}
