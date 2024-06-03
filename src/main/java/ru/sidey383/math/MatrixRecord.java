package ru.sidey383.math;

import org.jetbrains.annotations.NotNull;

public record MatrixRecord(double @NotNull [] @NotNull [] values) implements Matrix {

    public MatrixRecord {
        int height = values.length;
        if (values.length == 0)
            throw new IllegalArgumentException("Values can't be empty");
        int width = values[0].length;
        for (int i = 1; i < height; i++) {
            if (width != values[i].length)
                throw new IllegalArgumentException("Wrong matrix size, values[0].length=" + width + ", but values[" + i + "].length="+i);
        }
    }

    @Override
    public int rows() {
        return values.length;
    }

    @Override
    public int columns() {
        return values[0].length;
    }

    public double get(int i, int j) throws IllegalArgumentException {
        if (i < 0 || i >= rows() || j < 0 || j >= columns())
            throw new IllegalArgumentException("Wrong indexes (" + i + "," + j + ") for matrix (" + rows() + "," + columns() + ")");
        return values[i][j];
    }

}
