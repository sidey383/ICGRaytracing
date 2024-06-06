package ru.sidey383.math;

public record Vector3(double x, double y, double z) implements Vector {

    public Vector3(float x, float y, float z) {
        this((double) x, (double) y, (double) z);
    }

    public Vector3(float... val) {
        this(val[0], val[1], val[2]);
    }

    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    public static final Vector3 X = new Vector3(1, 0 ,0);
    public static final Vector3 Y = new Vector3(0, 1, 0);
    public static final Vector3 Z = new Vector3(0, 0, 1);

    public int size() {
        return 3;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3( 
                y* v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

    public Vector toVector4() {
        return new VectorRecord(x, y, z, 1);
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 add(double val) {
        return new Vector3(x + val, y + val, z + val);
    }

    public Vector3 mul(double num) {
        return new Vector3(x * num, y * num, z * num);
    }

    public Vector3 mul(Vector3 vector3) {
        return new Vector3(x * vector3.x, y * vector3.y, z * vector3.z);
    }

    public Vector3 normalize() {
        double len = Math.sqrt(x * x + y * y + z * z);
        return new Vector3(x / len, y / len, z / len);
    }

    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 sub(double val) {
        return new Vector3(x - val, y - val, z - val);
    }

    public double dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3 v) {
        return sub(v).length();
    }

    private static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    private static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    @Deprecated
    public double get(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IndexOutOfBoundsException();
        };
    }

}
