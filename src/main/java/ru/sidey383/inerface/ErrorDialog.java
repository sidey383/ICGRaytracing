package ru.sidey383.inerface;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDialog {

    private JDialog dialog;

    private final String title;

    public ErrorDialog(String title) {
        this.title = title;
    }

    public void show(String text) {
       if (dialog != null && dialog.isShowing())
            return;
        dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setPreferredSize(new Dimension(400, 100));
        dialog.setMinimumSize(new Dimension(200, 100));
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        dialog.add(textArea, BorderLayout.CENTER);
        dialog.pack();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(false);
    }

    public void show(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        show(sStackTrace);
    }

}