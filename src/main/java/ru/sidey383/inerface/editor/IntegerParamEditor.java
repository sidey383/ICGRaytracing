package ru.sidey383.inerface.editor;

import ru.sidey383.inerface.param.IntegerParam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;

public class IntegerParamEditor extends JPanel {

    private final JTextField textField;

    private final JSlider slider;

    private final IntegerParam param;

    private final NumberFormat amountFormat;

    public IntegerParamEditor(IntegerParam param) {
        super();
        this.param = param;
        setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel(param.name());
        add(label);
        amountFormat = NumberFormat.getNumberInstance();
        amountFormat.setParseIntegerOnly(true);
        amountFormat.setGroupingUsed(true);
        textField = new JFormattedTextField(amountFormat);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}
            @Override
            public void focusLost(FocusEvent e) {
                textFieldUpdate();
            }
        });
        Dimension size = new Dimension(Math.max(30, 8 + 10 * (int) Math.log10(param.max())), 20);
        textField.setMinimumSize(size);
        textField.setSize(size.width , size.height);
        textField.setPreferredSize(size);
        add(textField);
        textField.setText(amountFormat.format(param.getValue()));
        textField.addActionListener(this::updateField);
        slider = new JSlider(param.min(), param.max(), param.getValue());
        add(slider);
        slider.addChangeListener(this::updateSlider);
    }

    private void updateField(ActionEvent e) {
        textFieldUpdate();
    }

    private void textFieldUpdate() {
        try {
            int val = amountFormat.parse(textField.getText()).intValue();
            if (val < param.min()) {
                textField.setText(amountFormat.format(param.min()));
                wrongDataDialog();
                val = param.min();
            }
            if (val > param.max()) {
                textField.setText(amountFormat.format(param.max()));
                wrongDataDialog();
                val = param.max();
            }
            slider.setValue(val);
        } catch (ParseException ex) {
            textField.setText(amountFormat.format(param.getDefault()));
            wrongDataDialog();
            slider.setValue(param.min());
        }
    }

    private void updateSlider(ChangeEvent e) {
        int val = slider.getValue();
        textField.setText(amountFormat.format(val));
        setValue(val);
    }

    public void wrongDataDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Wrong data");
        dialog.setPreferredSize(new Dimension(400, 100));
        dialog.setMinimumSize(new Dimension(400, 100));
        dialog.add(new JLabel("Only integer values from " + param.min() + " to " + param.max() + " are available"), BorderLayout.CENTER);
        dialog.pack();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }


    protected void setValue(int val) {
        param.setValue(val);
    }

}
