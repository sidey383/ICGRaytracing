package ru.sidey383.math;

public interface Matrix {

    /**
     * @throws IllegalArgumentException on wrong vector size
     * **/
    default Vector multiply(Vector v) throws IllegalArgumentException {
        if (v.size() != columns())
            throw new IllegalArgumentException("Wrong vector size, actual " + v.size() + ", but expect " + columns());
        double[] result = new double[v.size()];
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                result[i] += v.get(j) * get(i, j);
            }
        }
        return new VectorRecord(result);
    }

    default Matrix multiply(Matrix m) throws IllegalArgumentException {
        if (columns() != m.rows())
            throw new IllegalArgumentException("Wrong matrix size, actual " + m.rows() + ", but expect " + columns());
        double[][] newMatrix = new double[rows()][m.columns()];
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                for (int r = 0; r < columns(); r++) {
                    newMatrix[i][j] += get(i, r) * m.get(r, j);
                }
            }
        }
        return new MatrixRecord(newMatrix);
    }

    int rows();

    int columns();

    double get(int i, int j) throws IllegalArgumentException;

}
