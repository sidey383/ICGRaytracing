package ru.sidey383.inerface.view;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.model.ApplicationParameters;
import ru.sidey383.render.linemodel.paint.LinesPainter;
import ru.sidey383.render.raytrace.RaytracingRender;
import ru.sidey383.render.raytrace.RaytraceConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class RenderSceneView extends JPanel {

    @Getter
    private final RaytracingRender raytracing;
    private final BufferedImage image;
    private ApplicationParameters parameters;
    private final Timer imageUpdateTimer;
    private final RenderStatusDialog statusDialog;
    private final LinesPainter linesPainter = new LinesPainter();
    private boolean isDrawLines = false;

    @Getter
    @NotNull
    private final JScrollPane scrollPane;

    public RenderSceneView(@NotNull ApplicationParameters parameters, @NotNull BufferedImage image) {
        this.image = image;
        this.parameters = parameters;
        RaytraceConfiguration configuration = new RaytraceConfiguration(
                image.getWidth(),
                image.getHeight(),
                parameters.getCamera().toFinalCamera(),
                parameters.getRaytraceSettings().getQuality(),
                parameters.getSceneState().getObjects(),
                parameters.getSceneState().getLight(),
                parameters.getRaytraceSettings().getTraceDeep(),
                parameters.getRaytraceSettings().getAmbient(),
                parameters.getRaytraceSettings().getBackground()
        );
        raytracing = new RaytracingRender(configuration, Runtime.getRuntime().availableProcessors() * 4);
        imageUpdateTimer = new Timer(1000 / 3, this::updateImage);
        statusDialog = new RenderStatusDialog(raytracing, this::stopRender);
        statusDialog.showStatus();
        setSizes();
        scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    }

    public RenderSceneView(@NotNull ApplicationParameters parameters, int width, int height) {
        this(parameters, new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
    }

    public void startRender() {
        raytracing.startRender(this::onComplete);
        imageUpdateTimer.start();
        statusDialog.setAlwaysOnTop(true);
        statusDialog.setVisible(true);
    }

    public void stopRender() {
        raytracing.shutdown();
        statusDialog.stop();
    }

    public void syncRepaint() {
        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        }
        );
    }

    public void setDrawLines(boolean drawLines) {
        this.isDrawLines = drawLines;
        updateImage();
    }

    private void setSizes() {
        setSize(new Dimension(image.getWidth(), image.getHeight()));
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
        setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        drawOnImage(image);
        return image;
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

    private void updateImage(ActionEvent e) {
        updateImage();
    }

    public void updateImage() {
        drawOnImage(image);
        syncRepaint();
    }

    private void onComplete(RaytracingRender.RenderStatus status) {
        statusDialog.acceptStatus(status);
        imageUpdateTimer.stop();
        drawOnImage(image);
        syncRepaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
