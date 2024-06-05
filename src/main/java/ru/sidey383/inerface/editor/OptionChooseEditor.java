package ru.sidey383.inerface.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class OptionChooseEditor extends JPanel {

    private final ButtonGroup group;

    private final Option[] options;

    public OptionChooseEditor(Option... options) {
        this.options = options;
        setLayout(new FlowLayout(FlowLayout.CENTER));
        group = new ButtonGroup();
        for (Option option : options) {
            group.add(option);
            add(option);
        }
    }

    public void setSelected(int i) {
        if (i < 0 || i >= options.length)
            return;
        group.setSelected(options[i].getModel(), true);
    }

    public abstract static class Option extends JToggleButton implements ItemListener {

        public Option(String name) {
            super(name);
            addItemListener(this);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            select();
        }

        public abstract void select();

    }

}
