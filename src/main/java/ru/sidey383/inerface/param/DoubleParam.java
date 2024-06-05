package ru.sidey383.inerface.param;

import ru.sidey383.inerface.AbstractParam;
import ru.sidey383.inerface.editor.DoubleParamEditor;

import javax.swing.*;

public class DoubleParam extends AbstractParam<Double> {

    private final double min;

    private final double max;

    private final int divisions;

    private final DoubleParamEditor editor;

    public DoubleParam(String name, double value, double min, double max, int divisions) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.divisions = divisions;
        this.editor = new DoubleParamEditor(this);
    }

    @Override
    public JComponent editorComponent() {
        return editor;
    }

    public double max() {
        return max;
    }

    public double min() {
        return min;
    }

    public int getDivisions() {
        return divisions;
    }
}
