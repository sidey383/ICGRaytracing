package ru.sidey383.render.linemodel.model;

import ru.sidey383.math.Matrix;
import ru.sidey383.math.Vector;

import java.util.ArrayList;
import java.util.List;

public interface LinesSupplier {

    /**
     * @return list of pairs
     * **/
    default List<Pair<Vector>> calculateLines(Matrix transformation) {
        List<Pair<Vector>> vectorLine = getLines();
        return vectorLine.stream().map(pair -> pair.apply(transformation::multiply)).toList();
    }

    List<Pair<Vector>> getLines();

    default LinesSupplier composition(LinesSupplier supplier) {
        return () -> {
            List<Pair<Vector>> lines = new ArrayList<>();
            lines.addAll(getLines());
            lines.addAll(supplier.getLines());
            return lines;
        };
    }

}
