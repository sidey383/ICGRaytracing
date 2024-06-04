package ru.sidey383.inerface.panel;

import ru.sidey383.camera.EditableCamera;
import ru.sidey383.inerface.FrameInteraction;
import ru.sidey383.inerface.ShowFrameActionListener;
import ru.sidey383.render.linemodel.LinesPainter;
import ru.sidey383.render.linemodel.model.LinesSupplier;
import ru.sidey383.render.linemodel.paint.PerspectiveLinesPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class LineSceneView extends JPanel implements FrameInteraction {

    private final LinesPainter painter;

    private final EditableCamera camera;

    private final List<? extends LinesSupplier> linesSuppliers;

    public LineSceneView(EditableCamera camera, List<? extends LinesSupplier> suppliers) {
        this.camera = camera;
        this.painter = new PerspectiveLinesPainter(camera);
        this.linesSuppliers = suppliers;
        ShowFrameActionListener listener = new ShowFrameActionListener(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);
        addKeyListener(listener);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getVisibleRect().getSize();
        if (size.height == 0 || size.width == 0)
            return;
        int width = size.width;
        int height = size.height;
        if (g instanceof  Graphics2D g2) {
            painter.createImage(linesSuppliers, g2, width, height);
        } else {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2 = image.createGraphics();
            painter.createImage(linesSuppliers, g2, width, height);
            g2.dispose();
            g.drawImage(image, 0, 0, null);
        }
    }

    @Override
    public void rotateCamera(double xRot, double yRot) {
        camera.rotate(xRot, yRot);
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
        camera.goForward(val);
        this.repaint();
    }

    @Override
    public void move(double x, double y) {
        camera.move(x, y);
        this.repaint();
    }

    @Override
    public void zoom(double value) {
        camera.zoom(value);
        this.repaint();
    }
}
