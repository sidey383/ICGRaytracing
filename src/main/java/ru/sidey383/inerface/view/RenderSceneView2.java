package ru.sidey383.inerface.view;

import ru.sidey383.model.ApplicationParameters;
import ru.sidey383.render.linemodel.paint.LinesPainter;
import ru.sidey383.render.raytrace.RaytraceConfiguration;
import ru.sidey383.render.raytrace.RaytracingRender;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class RenderSceneView2 extends JPanel implements MouseListener, MouseMotionListener {

    private final ApplicationParameters parameters;
    private final LinesPainter linesPainter = new LinesPainter();
    private BufferedImage buffer;
    private RaytracingRender raytracing;
    private Timer updateTimer;
    private RenderStatusDialog statusDialog;
    private final int EMPTY_PADDING = 4;
    private final int EFFECTIVE_PADDING = EMPTY_PADDING + 1;
    public JScrollPane scrollPane = new JScrollPane();
    private boolean isAdaptive = false;
    private boolean isDraggingEnabled = true;
    private boolean isDrawLines = false;

    public RenderSceneView2(ApplicationParameters parameters) {
        this.parameters = parameters;
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(EMPTY_PADDING, EMPTY_PADDING, EMPTY_PADDING, EMPTY_PADDING),
                BorderFactory.createDashedBorder(Color.BLACK, 5, 2)
        );
        setBorder(border);
        addMouseListener(this);
        addMouseMotionListener(this);
        scrollPane.setViewportView(this);
    }

    public void renderImage(BufferedImage origin) {
        if (raytracing != null)
            raytracing.shutdown();
        buffer = origin;
        if (buffer == null) {
            setPreferredSize(null);
            setSize(0, 0);
            scrollPane.revalidate();
            scrollPane.repaint();
            return;
        }
        if (!isAdaptive) {
            int width = buffer.getWidth() + 2 * (EFFECTIVE_PADDING);
            int height = buffer.getHeight() + 2 * (EFFECTIVE_PADDING);
            setPreferredSize(new Dimension(width, height));
            setSize(width, height);
        }
        else {
            setPreferredSize(null);
            setSize(0, 0);
        }
        scrollPane.revalidate();
        scrollPane.repaint();
        RaytraceConfiguration configuration = new RaytraceConfiguration(
                buffer.getWidth(),
                buffer.getHeight(),
                parameters.getCamera().toFinalCamera(),
                parameters.getRaytraceSettings().getQuality(),
                parameters.getSceneState().getObjects(),
                parameters.getSceneState().getLight(),
                parameters.getRaytraceSettings().getTraceDeep(),
                parameters.getRaytraceSettings().getAmbient(),
                parameters.getRaytraceSettings().getBackground()
        );
        raytracing = new RaytracingRender(configuration, Runtime.getRuntime().availableProcessors() * 4);
        updateTimer = new Timer(1000 / 3, (a) -> this.updateImage());
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
        if (adaptive) {
            setPreferredSize(null);
            setSize(0, 0);
        }
        else if (buffer != null) {
            int width = buffer.getWidth() + 2 * (EFFECTIVE_PADDING);
            int height = buffer.getHeight() + 2 * (EFFECTIVE_PADDING);
            setPreferredSize(new Dimension(width, height));
            setSize(width, height);
        }
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    public void setDraggingEnabled(boolean draggingEnabled) {
        isDraggingEnabled = draggingEnabled;
    }

    public void updateImage() {
        drawOnImage(buffer);
        syncRepaint();
    }

    public void setDrawLines(boolean drawLines) {
        this.isDrawLines = drawLines;
        updateImage();
    }

    public void syncRepaint() {
        SwingUtilities.invokeLater(() -> {
                    this.revalidate();
                    this.repaint();
                }
        );
    }

    private void drawOnImage(BufferedImage image) {
        raytracing.drawImage(image, parameters.getRaytraceSettings().getGamma());
        if (isDrawLines) {
            Graphics2D gr = image.createGraphics();
            gr.setColor(Color.BLACK);
            linesPainter.drawOnlyLines(parameters.getCamera(), parameters.getSceneState().getObjects(), gr, image.getWidth(), image.getHeight());
            gr.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer == null)
            return;
        int x = EFFECTIVE_PADDING;
        int y = EFFECTIVE_PADDING;
        if (!isAdaptive) {
            g.drawImage(buffer, x, y, this);
        }
        else {
            Dimension viewSize = scrollPane.getViewport().getSize();
            viewSize.width -= 2*EFFECTIVE_PADDING;
            viewSize.height -= 2*EFFECTIVE_PADDING;
            float image_ratio = (float) buffer.getWidth() / buffer.getHeight();
            float panel_ratio = (float) viewSize.width / viewSize.height;
            int newWidth, newHeight;
            if (image_ratio > panel_ratio) {
                newWidth = viewSize.width;
                newHeight = Math.round(newWidth / image_ratio);
            }
            else {
                newHeight = viewSize.height;
                newWidth = Math.round(newHeight * image_ratio);
            }
            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            graphics2D.drawImage(buffer, x, y, newWidth, newHeight, null);
            graphics2D.dispose();
        }
    }

    private int lastX = 0;
    private int lastY = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        if (isDraggingEnabled && (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            lastX = e.getX();
            lastY = e.getY();
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == 0 || !isDraggingEnabled) {
            return;
        }

        Point scroll = scrollPane.getViewport().getViewPosition();
        scroll.x += ( lastX - e.getX() );
        scroll.y += ( lastY - e.getY() );
        lastX = e.getX() + (lastX - e.getX());	// lastX = lastX
        lastY = e.getY() + (lastY - e.getY());	// lastY = lastY
        scrollPane.getHorizontalScrollBar().setValue(scroll.x);
        scrollPane.getVerticalScrollBar().setValue(scroll.y);
        scrollPane.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDraggingEnabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (isDraggingEnabled)
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

}
