package ru.sidey383.math;

public record QuaternionRotation(double x, double y, double z, double w) {

    public QuaternionRotation(Vector3 v, double angle) {
        this(
                v.get(0) / v.length() * Math.sin(angle / 2),
                v.get(1) / v.length() * Math.sin(angle / 2),
                v.get(2) / v.length() * Math.sin(angle / 2),
                Math.cos(angle / 2)
        );
    }

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

    public Vector3 mult(Vector3 b) {
        return new Vector3Record(
                w * b.x() + y * b.z() - z * b.y(),
                w * b.y() - x * b.z() + z * b.x(),
                w * b.z() + x * b.y() - y * b.x()
        );
    }

    public QuaternionRotation mult(QuaternionRotation b) {
        return new QuaternionRotation(
                w * b.x + x * b.w + y * b.z - z * b.y,
                w * b.y - x * b.z + y * b.w + z * b.x,
                w * b.z + x * b.y - y * b.x + z * b.w,
                w * b.w - x * b.x - y * b.y - z * b.z
        );
    }

}
