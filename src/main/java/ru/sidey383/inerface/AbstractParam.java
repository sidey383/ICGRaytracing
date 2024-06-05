package ru.sidey383.inerface;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AbstractParam<T> {

    private final String name;

    private final T base;

    private T value;

    public AbstractParam(String name, T value) {
        this.value = value;
        this.base = value;
        this.name = name;
    }

    public void setValue(@NotNull T value) {
        this.value = value;
    }

    @NotNull
    public T getValue() {
        return this.value;
    }

    public T getDefault() {
        return base;
    }

    public String name() {
        return name;
    }

    public abstract JComponent editorComponent();

}
