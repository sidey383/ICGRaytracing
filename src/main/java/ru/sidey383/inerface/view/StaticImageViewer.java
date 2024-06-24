package ru.sidey383.inerface.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StaticImageViewer extends JPanel {

    private final BufferedImage image;

    public StaticImageViewer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getSize();
        if (dim == null || dim.height <= 0 || dim.width <= 0)
            return;
        int newHeight = dim.height;
        int newWidth = dim.height * image.getWidth() / image.getHeight();
        int x = (dim.width - newWidth) / 2;
        Graphics2D graphics2D = (Graphics2D) g.create();
        graphics2D.drawImage(image, x, 0, newWidth, newHeight, null);
        graphics2D.dispose();
    }

}
