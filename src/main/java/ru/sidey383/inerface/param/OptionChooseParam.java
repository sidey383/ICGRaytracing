package ru.sidey383.inerface.param;

import org.jetbrains.annotations.NotNull;
import ru.sidey383.inerface.AbstractParam;
import ru.sidey383.inerface.editor.OptionChooseEditor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OptionChooseParam<T> extends AbstractParam<T> {

    private final OptionChooseEditor editor;

    public OptionChooseParam(String name, @NotNull T value, List<T> values, List<String> names) {
        super(name, value);
        List<OptionChooseEditor.Option> options = new ArrayList<>();
        int selectedNum = -1;
        for (int i = 0; i < values.size(); i++) {
            int finalI = i;
            options.add(new OptionChooseEditor.Option(names.get(i)) {
                @Override
                public void select() {
                    setValue(values.get(finalI));
                }
            });
            if (values.get(finalI) == value) {
                selectedNum = i;
            }
        }
        if (selectedNum == -1) {
            throw new IllegalArgumentException("Can't find value in available options");
        }
        editor = new OptionChooseEditor(options.toArray(new OptionChooseEditor.Option[0]));
        editor.setSelected(selectedNum);
    }

    @Override
    public JComponent editorComponent() {
        return editor;
    }
}
