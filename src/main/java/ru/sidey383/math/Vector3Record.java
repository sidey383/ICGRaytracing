package ru.sidey383.math;

public record Vector3Record(double x, double y, double z) implements Vector3 {
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
