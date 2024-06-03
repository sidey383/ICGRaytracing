package ru.sidey383.math;

public record Vector3Record(double x, double y, double z) implements Vector3 {

    public static Vector3 crop(Vector v) {
        if (v.size() < 3)
            throw new IllegalArgumentException("Vector too small for crop");
        return new Vector3Record(v.get(0), v.get(1), v.get(2));
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public double get(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IllegalArgumentException("Index out of bounds");
        };
    }
}
