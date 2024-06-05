package ru.sidey383.inerface.view;

import ru.sidey383.render.raytrace.RaytracingRender;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RenderStatusDialog extends JDialog {

    private final JProgressBar progressBar;
    private final JTextField errorField;
    private final JLabel isRunning;
    private final JLabel isComplete;
    private final JLabel isFailed;

    public RenderStatusDialog(RaytracingRender.RenderStatus status, Runnable interrupt) {
        setTitle("Render status");
        errorField = new JTextField();
        errorField.setEditable(false);
        progressBar = new JProgressBar();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        isRunning = new JLabel();
        isComplete = new JLabel();
        isFailed = new JLabel();
        JButton interruptButton = new JButton("Interrupt");
        interruptButton.addActionListener((a) -> interrupt.run());
        add(interruptButton);
        add(progressBar);
        add(isComplete);
        add(isRunning);
        add(isFailed);
        acceptStatus(status);
        pack();
        setMinimumSize(new Dimension(300, 200));

    }

    public void acceptStatus(RaytracingRender.RenderStatus status) {
        progressBar.setValue(status.completeCount());
        progressBar.setMaximum(status.totalCount());
        isRunning.setText(status.isRunning() ? "Running" : "Not running");
        isComplete.setText(status.isComplete() ? "Complete" : "Not complete");
        isFailed.setText(status.isFailed() ? "Failed" : "Not failed");
        if (status.error() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            status.error().printStackTrace(pw);
            String sStackTrace = sw.toString();
            errorField.setText(sStackTrace);
            add(errorField);
            pack();
            setMinimumSize(new Dimension(300, 200));
        }
    }

}
