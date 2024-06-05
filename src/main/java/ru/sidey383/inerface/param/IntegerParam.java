package ru.sidey383.inerface.param;


import ru.sidey383.inerface.AbstractParam;
import ru.sidey383.inerface.editor.IntegerParamEditor;

import javax.swing.*;

public class IntegerParam extends AbstractParam<Integer> {

    private final int min;

    private final int max;

    private final IntegerParamEditor editor;

    public IntegerParam(String name, int value, int min, int max) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.editor = new IntegerParamEditor(this);
    }

    @Override
    public JComponent editorComponent() {
        return editor;
    }

    public int max() {
        return max;
    }

    public int min() {
        return min;
    }

}
