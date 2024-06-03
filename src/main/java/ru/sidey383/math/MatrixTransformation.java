package ru.sidey383.math;

import lombok.Getter;

public enum MatrixTransformation {
    X(1, 2, -1), Y(0, 2, 1), Z(0, 1, -1);

    @Getter
    private static final Matrix noTransformation = getTransposition(0, 0, 0);

    private final int firstAxis;
    private final int secondAxis;
    private final int sign;

    MatrixTransformation(int firstAxis, int secondAxis, int sign) {
        this.firstAxis = firstAxis;
        this.secondAxis = secondAxis;
        this.sign = sign;
    }

    /**
     * @param angle in radians
     * **/
    public Matrix createRotationMatrix(double angle) {
        double[][] values = new double[4][4];
        for (int i = 0; i < 4; i++) {
            values[i][i] = 1;
        }
        values[firstAxis][firstAxis] = Math.cos(angle);
        values[secondAxis][secondAxis] = Math.cos(angle);
        values[firstAxis][secondAxis] = sign * Math.sin(angle);
        values[secondAxis][firstAxis] = -sign * Math.sin(angle);
        return new MatrixRecord(values);
    }

    public static Matrix getTransposition(double x, double y, double z) {
        double[][] values = new double[4][4];
        for (int i = 0; i < 4; i++) {
            values[i][i] = 1;
        }
        values[0][3] = x;
        values[1][3] = y;
        values[2][3] = z;
        return new MatrixRecord(values);
    }

}
