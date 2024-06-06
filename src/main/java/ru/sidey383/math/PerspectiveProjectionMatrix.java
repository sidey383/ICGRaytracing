package ru.sidey383.math;

public class PerspectiveProjectionMatrix implements Matrix {

    private final double[][] values;

    public PerspectiveProjectionMatrix(double hs, double ws, double n, double f) {
        values = new double[4][4];
        values[0][0] = n / ws;
        values[1][1] = n / hs;
        values[2][2] = f / (f - n);
        values[3][2] = 1;
        values[2][3] = -(f * n) / (f - n);
    }

    @Override
    public int rows() {
        return 4;
    }

    @Override
    public int columns() {
        return 4;
    }

    @Override
    public double get(int i, int j) {
        return values[i][j];
    }

    public Vector toScreen(Vector v) throws IllegalArgumentException {
        if (v.size() != 4)
            throw new IllegalArgumentException("Wrong vector size, actual " + v.size() + ", but expect " + columns());
        double[] result = new double[v.size()];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += v.get(j) * values[i][j];
            }
        }
        if (result[3] == 0)
            return new VectorRecord(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        return new VectorRecord(result[0] / result[3], result[1] / result[3], result[2] / result[3]);
    }

}
