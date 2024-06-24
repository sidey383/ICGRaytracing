package ru.sidey383.inerface.actions;

import lombok.Getter;
import ru.sidey383.inerface.SceneHolder;
import ru.sidey383.inerface.view.PreviewSceneView;
import ru.sidey383.inerface.view.RenderSceneView;
import ru.sidey383.model.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class SceneController extends JPanel implements SceneHolder {

    private final PreviewSceneView previewScene;
    private RenderSceneView renderScene;

    private final ApplicationParameters parameters;

    private final ButtonGroup group = new ButtonGroup();
    @Getter
    private final JRadioButtonMenuItem previewItem = new JRadioButtonMenuItem("Preview");
    @Getter
    private final JRadioButtonMenuItem renderItem = new JRadioButtonMenuItem("Render");

    public SceneController(ApplicationParameters parameters) {
        super();
        setLayout(new BorderLayout());
        this.parameters = parameters;
        this.previewScene = new PreviewSceneView(parameters);
        group.add(previewItem);
        group.add(renderItem);
        group.setSelected(previewItem.getModel(), true);
        previewItem.addActionListener(this::selectionUpdate);
        renderItem.addActionListener(this::selectionUpdate);
        add(previewScene, BorderLayout.CENTER);
    }

    private void selectionUpdate(ActionEvent e) {
        if (e.getSource() == previewItem) {
            if (renderScene != null) {
                remove(renderScene);
                renderScene.setFocusable(false);
                renderScene = null;
            }
            add(previewScene, BorderLayout.CENTER);
            previewScene.setFocusable(true);
            previewScene.requestFocus();
        } else if (e.getSource() == renderItem) {
            remove(previewScene);
            previewScene.setFocusable(false);
            renderScene = new RenderSceneView(parameters, previewScene.createImage());
            add(renderScene, BorderLayout.CENTER);
            renderScene.startRender();
            renderScene.setFocusable(true);
            renderScene.requestFocus();
        }
        revalidate();
        repaint();
    }

    @Override
    public Optional<RenderSceneView> getRenderSceneView() {
        return Optional.ofNullable(renderScene);
    }

    @Override
    public PreviewSceneView getPreviewSceneView() {
        return previewScene;
    }

}
