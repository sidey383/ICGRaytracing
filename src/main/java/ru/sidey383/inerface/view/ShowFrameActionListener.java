package ru.sidey383.inerface.view;

import ru.sidey383.interfaceV2.CameraInteractions;

import java.awt.event.*;

public class ShowFrameActionListener extends MouseAdapter implements KeyListener {

    private final CameraInteractions interaction;

    private int lastX = 0;

    private int lastY = 0;

    private boolean isClicked = false;

    public ShowFrameActionListener(CameraInteractions interaction) {
        this.interaction = interaction;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isClicked = true;
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isClicked) {
            int dx = e.getX() - lastX;
            int dy = e.getY() - lastY;
            double xRot = 2 * Math.PI * dx / interaction.screenWidth();
            double yRot = 2 * Math.PI * dy / interaction.screenHeight();
            interaction.rotateCamera(xRot, -yRot);
        }
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isClicked = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            interaction.goForward(e.getWheelRotation() * 0.5);
        } else {
            interaction.zoom(e.getWheelRotation() * 0.05);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyAction(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyAction(e);
    }

    private void keyAction(KeyEvent e) {
        if (e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_UP) {
            interaction.move(0, 0.05);
        }
        if (e.getKeyChar() == 's' || e.getKeyCode() == KeyEvent.VK_DOWN) {
            interaction.move(0, -0.05);
        }
        if (e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) {
            interaction.move(-0.05, 0);
        }
        if (e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            interaction.move(0.05, 0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
