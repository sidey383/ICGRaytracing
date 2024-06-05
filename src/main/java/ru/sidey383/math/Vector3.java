package ru.sidey383.math;

public interface Vector3 extends Vector {

    Vector3 ZERO = new Vector3Record(0, 0, 0);

    Vector3 X = new Vector3Record(1, 0 ,0);
    Vector3 Y = new Vector3Record(0, 1, 0);
    Vector3 Z = new Vector3Record(0, 0, 1);

    default int size() {
        return 3;
    }

    default Vector3 cross(Vector3 v) {
        return new Vector3Record(
            get(1) * v.get(2) - get(2) * v.get(1),
            get(2) * v.get(0) - get(0) * v.get(2),
            get(0) * v.get(1) - get(1) * v.get(0)
        );
    }

    default Vector3 rotate(Vector3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = get(0) * (cos + (1 - cos) * v.get(0) * v.get(0)) + get(1) * ((1 - cos) * v.get(0) * v.get(1) - sin * v.get(2)) + get(2) * ((1 - cos) * v.get(0) * v.get(2) + sin * v.get(1));
        double y = get(0) * ((1 - cos) * v.get(0) * v.get(1) + sin * v.get(2)) + get(1) * (cos + (1 - cos) * v.get(1) * v.get(1)) + get(2) * ((1 - cos) * v.get(1) * v.get(2) - sin * v.get(0));
        double z = get(0) * ((1 - cos) * v.get(0) * v.get(2) - sin * v.get(1)) + get(1) * ((1 - cos) * v.get(1) * v.get(2) + sin * v.get(0)) + get(2) * (cos + (1 - cos) * v.get(2) * v.get(2));
        return new Vector3Record(x, y, z);
    }

    default Vector toVector4() {
        return new VectorRecord(get(0), get(1), get(2), 1);
    }

    default Vector3 add(Vector3 v) {
        return new Vector3Record(get(0) + v.get(0), get(1) + v.get(1), get(2) + v.get(2));
    }

    default Vector3 add(double val) {
        return new Vector3Record(get(0) + val, get(1) + val, get(2) + val);
    }

    default Vector3 mul(double num) {
        return new Vector3Record(get(0) * num, get(1) * num, get(2) * num);
    }

    default Vector3 mul(Vector3 vector3) {
        return new Vector3Record(get(0) * vector3.get(0), get(1) * vector3.get(1), get(2) * vector3.get(2));
    }

    default Vector3 normalize() {
        double len = Math.sqrt(get(0) * get(0) + get(1) * get(1) + get(2) * get(2));
        return new Vector3Record(get(0) / len, get(1) / len, get(2) / len);
    }

    default Vector3 sub(Vector3 v) {
        return new Vector3Record(get(0) - v.get(0), get(1) - v.get(1), get(2) - v.get(2));
    }

    default Vector3 sub(double val) {
        return new Vector3Record(get(0) - val, get(1) - val, get(2) - val);
    }

    default double dot(Vector3 v) {
        return get(0) * v.get(0) + get(1) * v.get(1) + get(2) * v.get(2);
    }

    default double length() {
        return Math.sqrt(get(0) * get(0) + get(1) * get(1) + get(2) * get(2));
    }

    default double distance(Vector3 v) {
        return sub(v).length();
    }

    private static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    private static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    default double x() {
        return get(0);
    }

    default double y() {
        return get(1);
    }
    default double z() {
        return get(2);
    }

    Vector3 clone();

}
