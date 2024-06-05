package ru.sidey383.inerface.view;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.model.ApplicationParameters;
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
    private final Timer statusUpdateTimer;
    private final Timer imageUpdateTimer;
    private final RenderStatusDialog statusDialog;

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
        raytracing = new RaytracingRender(configuration, Runtime.getRuntime().availableProcessors());
        statusUpdateTimer = new Timer(100, this::updateStatus);
        imageUpdateTimer = new Timer(1000 / 3, this::updateImage);
        statusDialog = new RenderStatusDialog(raytracing.getStatus(), this::stopRender);
        setSizes();
        scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    }

    public RenderSceneView(@NotNull ApplicationParameters parameters, int width, int height) {
        this(parameters, new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
    }

    public void startRender() {
        raytracing.startRender(this::onComplete);
        statusUpdateTimer.start();
        imageUpdateTimer.start();
        statusDialog.setAlwaysOnTop(true);
        statusDialog.setVisible(true);
    }

    public void stopRender() {
        raytracing.shutdown();
    }

    public void syncRepaint() {
        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        }
        );
    }

    public void updateGamma() {
        raytracing.drawImage(image, parameters.getRaytraceSettings().getGamma());
        syncRepaint();
    }

    private void setSizes() {
        setSize(new Dimension(image.getWidth(), image.getHeight()));
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
        setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public void updateStatus(ActionEvent e) {
        statusDialog.acceptStatus(raytracing.getStatus());
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        raytracing.drawImage(image, parameters.getRaytraceSettings().getGamma());
        return image;
    }

    private void updateImage(ActionEvent e) {
        raytracing.drawImage(image, parameters.getRaytraceSettings().getGamma());
        syncRepaint();
    }

    private void onComplete(RaytracingRender.RenderStatus status) {
        statusDialog.acceptStatus(status);
        statusUpdateTimer.stop();
        imageUpdateTimer.stop();
        raytracing.drawImage(image, parameters.getRaytraceSettings().getGamma());
        syncRepaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
