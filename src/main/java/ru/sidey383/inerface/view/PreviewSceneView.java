package ru.sidey383.inerface.view;

import ru.sidey383.inerface.CameraInteractions;
import ru.sidey383.model.ApplicationParameters;
import ru.sidey383.render.linemodel.paint.LinesPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewSceneView extends JPanel implements CameraInteractions {

    private final LinesPainter painter = new LinesPainter();

    private final ApplicationParameters parameters;

    public PreviewSceneView(ApplicationParameters parameters) {
        this.parameters = parameters;;
        ShowFrameActionListener listener = new ShowFrameActionListener(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);
        addKeyListener(listener);
    }

    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gr = image.createGraphics();
        paintComponent(gr);
        gr.dispose();
        return image;
    }

    @Override
    public void paintComponent(Graphics g) {
        Dimension size = getSize();
        if (size.height == 0 || size.width == 0)
            return;
        int width = size.width;
        int height = size.height;
        if (g instanceof  Graphics2D g2) {
            painter.createImage(parameters.getCamera(), parameters.getSceneState().getObjects(), g2, width, height);
        } else {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2 = image.createGraphics();
            painter.createImage(parameters.getCamera(), parameters.getSceneState().getObjects(), g2, width, height);
            g2.dispose();
            g.drawImage(image, 0, 0, null);
        }
    }

    @Override
    public void rotateCamera(double xRot, double yRot) {
        parameters.getCamera().rotate(xRot, yRot);
        this.revalidate();
        this.repaint();
    }

    @Override
    public int screenWidth() {
        return getWidth();
    }

    @Override
    public int screenHeight() {
        return getHeight();
    }

    @Override
    public void goForward(double val) {
        parameters.getCamera().goForward(val);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void move(double x, double y) {
        parameters.getCamera().move(x, y);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void zoom(double value) {
        parameters.getCamera().zoom(value);
        this.revalidate();
        this.repaint();
    }
}
