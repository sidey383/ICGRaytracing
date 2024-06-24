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
        float image_ratio = (float) image.getWidth() / image.getHeight();
        float panel_ratio = (float) dim.width / dim.height;
        int newWidth, newHeight;
        if (image_ratio > panel_ratio) {
            newWidth = dim.width;
            newHeight = Math.round(newWidth / image_ratio);
        }
        else {
            newHeight = dim.height;
            newWidth = Math.round(newHeight * image_ratio);
        }
        Graphics2D graphics2D = (Graphics2D) g.create();
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
    }

}
