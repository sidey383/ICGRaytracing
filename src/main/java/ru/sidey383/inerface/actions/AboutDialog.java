package ru.sidey383.inerface.actions;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Objects;

public class AboutDialog extends JDialog {

    public AboutDialog() {
        super();
        setTitle("About");
        setSizeAndPosition();
        String html;
        try (InputStream is = Objects.requireNonNull(AboutDialog.class.getResource("/help.html")).openStream()) {
            html = new String(is.readAllBytes());
        } catch (Exception e) {
            html = "Load error";
        }
        add(new JLabel(html));
        pack();
    }

    public void showHelp() {
        if (!isShowing()) {
            setVisible(true);
        } else {
            setAlwaysOnTop(true);
            setAlwaysOnTop(false);
        }
    }

    private void setSizeAndPosition() {
        setMinimumSize(new Dimension(400, 500));
        setPreferredSize(new Dimension(400, 500));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 400, screenSize.height / 2 - 400);
    }

}
