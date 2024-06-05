package ru.sidey383.inerface.editor;

import ru.sidey383.inerface.param.DoubleParam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;

public class DoubleParamEditor extends JPanel {

    private final JTextField textField;

    private final JSlider slider;

    private final DoubleParam param;

    private final double defaultValue;

    private JDialog dialog;

    private boolean sliderInUpdate = false;

    private boolean textInUpdate = false;

    private final NumberFormat amountFormat;

    public DoubleParamEditor(DoubleParam param) {
        super();
        this.defaultValue = param.getValue();
        this.param = param;
        setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel(param.name());
        add(label);
        amountFormat = NumberFormat.getNumberInstance();
        amountFormat.setParseIntegerOnly(false);
        textField = new JFormattedTextField(amountFormat);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}
            @Override
            public void focusLost(FocusEvent e) {
                textFieldUpdate();
            }
        });
        Dimension size = new Dimension(Math.max(30, 6*5 + 8 * (int) Math.log10(param.max())), 20);
        textField.setMinimumSize(size);
        textField.setSize(size.width , size.height);
        textField.setPreferredSize(size);
        add(textField);
        textField.setText(amountFormat.format(param.getValue()));
        textField.addActionListener(this::updateField);
        slider = new JSlider(
                0, param.getDivisions(),
                fitSlider(param.getValue())
        );
        add(slider);
        slider.addChangeListener(this::sliderUpdate);
    }

    private void updateField(ActionEvent e) {
        textFieldUpdate();
    }

    private void textFieldUpdate() {
        textInUpdate = true;
        String text = textField.getText();
        try {
            double val = amountFormat.parse(text).doubleValue();
            if (val < param.min()) {
                textField.setText(amountFormat.format(param.min()));
                wrongDataDialog();
                val = param.min();
            }
            if (val > param.max()) {
                textField.setText(amountFormat.format(param.max()));
                wrongDataDialog();
                param.max();
            }
            if (!sliderInUpdate) {
                slider.setValue(fitSlider(val));
                setValue(val);
            }
        } catch (ParseException ex) {
            textField.setText(amountFormat.format(defaultValue));
            wrongDataDialog();
            slider.setValue(fitSlider(defaultValue));
        } finally {
            textInUpdate = false;
        }
    }

    private void sliderUpdate(ChangeEvent e) {
        sliderInUpdate = true;
        int val = slider.getValue();
        if (!textInUpdate) {
            double value = getFromSlider(val);
            textField.setText(amountFormat.format(value));
            textFieldUpdate();
            setValue(value);
        }
        sliderInUpdate = false;
    }

    public void wrongDataDialog() {
        if (dialog != null && dialog.isShowing())
            return;
        dialog = new JDialog();
        dialog.setTitle("Wrong data");
        dialog.setPreferredSize(new Dimension(400, 100));
        dialog.setMinimumSize(new Dimension(400, 100));
        dialog.add(new JLabel("Only double values from " + param.min() + " to " + param.max() + " are available"), BorderLayout.CENTER);
        dialog.pack();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }


    protected void setValue(double val) {
        param.setValue(val);
    }

    private int fitSlider(double val) {
        return (int) (param.getDivisions() * (val - param.min()) / (param.max() - param.min()));
    }

    private double getFromSlider(int val) {
        return ((double) val / param.getDivisions()) * (param.max() - param.min()) + param.min();
    }

}
