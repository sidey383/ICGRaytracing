package ru.sidey383.inerface;

import ru.sidey383.camera.EditableCamera;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.linemodel.LinesPainter;
import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.paint.PerspectiveLinesPainter;
import ru.sidey383.math.Vector3;
import ru.sidey383.objects.DrawableObject;
import ru.sidey383.raytrace.ImageRaytracing;
import ru.sidey383.raytrace.LightSource;
import ru.sidey383.raytrace.RaytraceConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class RenderPanel extends JPanel implements FrameInteraction {

    private ImageRaytracing raytracing;

    private final LinesPainter painter;

    private final EditableCamera camera;

    private final List<DrawableObject> objectList;

    private final List<LinesSupplier> linesSuppliers;

    private final List<LightSource> lightSources;

    private Vector3 ambiant;

    private int traceDeep;

    private double gamma;

    public RenderPanel(SceneConfiguration sceneConfiguration, RenderConfiguration renderConfiguration) {
        this.camera = new EditableCamera(renderConfiguration);
        painter = new PerspectiveLinesPainter(camera);
        this.objectList = sceneConfiguration.objects();
        this.lightSources = sceneConfiguration.lights();
        this.linesSuppliers = sceneConfiguration.objects().stream().map(DrawableObject::getLineSupplier).toList();
        this.ambiant = sceneConfiguration.ambient();
        this.traceDeep = renderConfiguration.traceDepth();
        this.gamma = renderConfiguration.gamma();
        ShowFrameActionListener listener = new ShowFrameActionListener(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);
        addKeyListener(listener);
    }

    private boolean isRaytracing() {
        return raytracing != null;
    }

    private Optional<ImageRaytracing> raytracing() {
        return Optional.ofNullable(raytracing);
    }

    private void startRaytrace() {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gr = buffer.createGraphics();
        drawLines(gr, getWidth(), getHeight());
        RaytraceConfiguration configuration = new RaytraceConfiguration(
                camera.toFinalCamera(),
                objectList.stream().map(DrawableObject::getRaytraceObject).toList(),
                lightSources,
                traceDeep,
                ambiant
        );
        ImageRaytracing raytracing = new ImageRaytracing(buffer, configuration, Runtime.getRuntime().availableProcessors());
        raytracing.accept(() -> {
            raytracing.calculateImage();
            SwingUtilities.invokeLater(this::repaint);
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getVisibleRect().getSize();
        if (size.height == 0 || size.width == 0)
            return;
        int width = size.width;
        int height = size.height;
        if (isRaytracing()) {
            drawRaytracing(g, width, height);
        } else {
            drawLines(g, width, height);
        }
    }

    private void drawRaytracing(Graphics g, int width, int height) {
        if (raytracing.isComplete()) {
            BufferedImage image = raytracing.getImage();
            g.drawImage(image, 0, 0, null);
        } else {
            drawLines(g, width, height);
        }
    }

    private void drawLines(Graphics g, int width, int height) {
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
