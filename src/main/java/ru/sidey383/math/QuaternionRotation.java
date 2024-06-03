package ru.sidey383.math;

public record QuaternionRotation(double x, double y, double z, double w) {

    public QuaternionRotation(double x, double y, double z, double w) {
        double norm = Math.sqrt(x * x + y * y + z * z + w * w);
        if (norm != 0) {
            this.x = x / norm;
            this.y = y / norm;
            this.z = z / norm;
            this.w = w / norm;
        } else {
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.w = 1;
        }
    }

    public static QuaternionRotation fromMatrix(Matrix matrix) {
        double tr = matrix.get(0, 0) + matrix.get(1, 1) + matrix.get(2, 2);
        double S = Math.sqrt(tr + 1.0) * 2;
        if (S == 0) {
            return new QuaternionRotation(
                    (matrix.get(2, 1) - matrix.get(1, 2)),
                    (matrix.get(0, 2) - matrix.get(2, 0)),
                    (matrix.get(1, 0) - matrix.get(0, 1)),
                    0
            );
        }
        return new QuaternionRotation(
                (matrix.get(2, 1) - matrix.get(1, 2)) / S,
                (matrix.get(0, 2) - matrix.get(2, 0)) / S,
                (matrix.get(1, 0) - matrix.get(0, 1)) / S,
                S / 4
        );
    }

    public Matrix toRotationMatrix() {
        return new MatrixRecord(new double[][]{
                {1.0 - 2.0 * y * y - 2.0f * z * z, 2.0f * x * y - 2.0f * z * w, 2.0f * x * z + 2.0f * y * w, 0.0},
                {2.0 * x * y + 2.0 * z * w, 1.0 - 2.0 * x * x - 2.0 * z * z, 2.0 * y * z - 2.0 * x * w, 0.0},
                {2.0f * x * z - 2.0f * y * w, 2.0f * y * z + 2.0f * x * w, 1.0f - 2.0f * x * x - 2.0f * y * y, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

}
