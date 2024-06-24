package ru.sidey383.inerface.view;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class FinalImageViewer extends JPanel implements KeyListener, MouseWheelListener {

    @NotNull
    private final BufferedImage image;

    private double viewXCenter;

    private double viewYCenter;

    private double zoom;

    public FinalImageViewer(@NotNull BufferedImage image) {
        setFocusable(true);
        requestFocus();
        this.image = image;
        defaultScale();
        addKeyListener(this);
        addMouseWheelListener(this);
    }

    public void defaultScale() {
        viewXCenter = 0.5;
        viewYCenter = 0.5;
        zoom = 1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getSize();
        if (dim == null || dim.height <= 0 || dim.width <= 0)
            return;
        int fW = dim.width;
        int fH = dim.height;
        int iW = image.getWidth();
        int iH = image.getHeight();
        int rW = Math.max((int) (iW * zoom), 1);
        int rH = Math.max((int) (iH * zoom), 1);
        int iX = (int) (viewXCenter * fW - rW / 2.0);
        int iY = (int) (viewYCenter * fH - rH / 2.0);
        g.drawImage(image, iX, iY, rW, rH, null);
    }

    public void zoomInImage(int x, int y, int times) {
        double zoom = Math.min(4, this.zoom * Math.pow(1.1, times));
        if ((zoom - 1) * (this.zoom - 1) < 0) {
            zoom = 1;
        }
        this.zoom = zoom;
        repaint();
    }

    public void zoomOutImage(int x, int y, int times) {
        double zoom = Math.max(0.2, this.zoom / Math.pow(1.1, times));
        if ((zoom - 1) * (this.zoom - 1) < 0) {
            zoom = 1;
        }
        this.zoom = zoom;
        repaint();
    }

    public void moveImage(int x, int y) {
        double viewXCenter = Math.min(1.5, Math.max(-0.5, this.viewXCenter + x / 30.0));
        double viewYCenter = Math.min(1.5, Math.max(-0.5, this.viewYCenter + y / 30.0));
        if ((viewXCenter - 0.5)*(this.viewXCenter - 0.5) < 0)
            viewXCenter = 0.5;
        if ((viewYCenter - 0.5)*(this.viewYCenter - 0.5) < 0)
            viewYCenter = 0.5;
        this.viewXCenter = viewXCenter;
        this.viewYCenter = viewYCenter;
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyAction(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyAction(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            zoomInImage(e.getX(), e.getY(), -e.getWheelRotation());
        }
        if (e.getWheelRotation() > 0) {
            zoomOutImage(e.getX(), e.getY(), e.getWheelRotation());
        }
    }

    private void keyAction(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            moveImage(0, 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            moveImage(0, -1);
        }
        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveImage(1, 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveImage(-1, 0);
        }
    }
}
