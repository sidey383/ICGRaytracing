package ru.sidey383.linemodel.model;

import java.util.function.Function;

public record Pair<T>(T first, T second) {
    public <R> Pair<R> apply(Function<T, R> function) {
        return new Pair<>(function.apply(first), function.apply(second));
    }
}
