package ru.sidey383.inerface;

import javax.swing.*;

public class SimpleDialog extends JDialog {

    public SimpleDialog(String text) {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        add(new JLabel(text));
        pack();
        setVisible(true);
    }

}
