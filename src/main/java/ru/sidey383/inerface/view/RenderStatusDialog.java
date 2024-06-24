package ru.sidey383.inerface.view;

import ru.sidey383.render.raytrace.RaytracingRender;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RenderStatusDialog extends JDialog {

    private final JProgressBar progressBar = new JProgressBar();
    private final JTextField errorField = new JTextField();
    private final JLabel isRunning = new JLabel("Not running");
    private final JLabel isComplete = new JLabel("Not complete");
    private final JLabel isFailed = new JLabel("Not failed");
    private Timer timer;
    private Long endTime;
    private Long startTime;
    private final RaytracingRender render;

    public RenderStatusDialog(RaytracingRender render, Runnable interrupt) {
        setTitle("Render status");
        this.render = render;
        errorField.setEditable(false);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JButton interruptButton = new JButton("Interrupt");
        interruptButton.addActionListener((a) -> interrupt.run());
        add(interruptButton);
        progressBar.setAlignmentY(0.5f);
        isComplete.setAlignmentY(0.5f);
        isRunning.setAlignmentY(0.5f);
        isFailed.setAlignmentY(0.5f);
        errorField.setAlignmentY(0.5f);
        add(progressBar);
        add(isComplete);
        add(isRunning);
        add(isFailed);
        pack();
        setMinimumSize(new Dimension(300, 200));
    }

    public void showStatus() {
        startTime = System.currentTimeMillis();
        timer = new Timer(100, (a) -> acceptStatus(render.getStatus()));
        timer.start();
        setAlwaysOnTop(true);
        setVisible(true);
    }

    public void stop() {
        timer.stop();
        setVisible(false);
    }

    public void acceptStatus(RaytracingRender.RenderStatus status) {
        progressBar.setValue(status.completeCount());
        progressBar.setMaximum(status.totalCount());
        isRunning.setText(status.isRunning() ? "Running" : "Not running");
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
        if (endTime != null) {
            if (System.currentTimeMillis() - endTime > 5000) {
                timer.stop();
                if (status.isComplete()) {
                    setVisible(false);
                }
            }
        } else {
            if (!status.isRunning()) {
                endTime = System.currentTimeMillis();
                if (status.isComplete())
                    isComplete.setText("Complete in " + (endTime - startTime) + "ms");
                if (status.isFailed())
                    isComplete.setText("Failed in " + (endTime - startTime) + "ms");
                remove(progressBar);
                revalidate();
                repaint();
            }
        }
    }

}
