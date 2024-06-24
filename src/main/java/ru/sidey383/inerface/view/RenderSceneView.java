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
    private final ApplicationParameters parameters;
    private final Timer imageUpdateTimer;
    private final RenderStatusDialog statusDialog;
    private final LinesPainter linesPainter = new LinesPainter();
    private boolean isDrawLines = false;
    @NotNull
    private JPanel currentPanel;

    public RenderSceneView(@NotNull ApplicationParameters parameters, @NotNull BufferedImage image) {
        setLayout(new BorderLayout());
        if (parameters.getRaytraceSettings().isCustomSize()) {
            image = resize(image, parameters.getRaytraceSettings().getRenderWidth(), parameters.getRaytraceSettings().getRenderHeight());
        }
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
        setLayout(new BorderLayout());
        currentPanel = new StaticImageViewer(image);
        add(currentPanel, BorderLayout.CENTER);

    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance( newH * img.getWidth() / img.getHeight(), newH , Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g2d = dimg.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newW, newH);
        g2d.drawImage(tmp, (dimg.getWidth() - tmp.getWidth(null)) / 2, 0, null);
        g2d.dispose();

        return dimg;
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
                    remove(currentPanel);
                    if (raytracing.getStatus().isRunning()) {
                        currentPanel = new StaticImageViewer(image);
                    } else {
                        if (currentPanel instanceof FinalImageViewer fv) {
                            currentPanel = new FinalImageViewer(image, fv.getViewXCenter(), fv.getViewYCenter(), fv.getZoom());
                        } else {
                            Dimension dim = getSize();
                            if (dim == null || dim.width <= 0 || dim.height <= 0) {
                                dim = new Dimension(1000, 1000);
                            }
                            currentPanel = new FinalImageViewer(image, dim.width, dim.height);
                        }
                    }
                    add(currentPanel, BorderLayout.CENTER);
                    currentPanel.setFocusable(true);
                    currentPanel.requestFocus();
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

}
