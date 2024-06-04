package ru.sidey383.math;

public interface Vector {

    int size();

    double get(int i);

    default double dot(Vector v) throws IllegalArgumentException {
        if (v.size() != size())
            throw new IllegalArgumentException("Wrong vector size, actual " + v.size() + ", but expect " + size());
        double result = 0;
        for (int i = 0; i < size(); i++) {
            result += v.get(i) * get(i);
        }
        return result;
    }

    Vector clone();

}
