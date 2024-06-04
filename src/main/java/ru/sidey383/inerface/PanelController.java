package ru.sidey383.inerface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sidey383.camera.EditableCamera;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.inerface.panel.LineSceneView;
import ru.sidey383.inerface.panel.RenderSceneView;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.DrawableObject;
import ru.sidey383.render.objects.LightSource;

import java.util.List;

public class PanelController {

    @NotNull
    private EditableCamera camera;
    @NotNull
    private List<DrawableObject> objectList;
    @NotNull
    private List<LightSource> lightSources;
    @NotNull
    private Vector3 ambientColor;
    @NotNull
    private Vector3 backgroundColor;
    @NotNull
    private LineSceneView lineSceneView;
    @Nullable
    private RenderSceneView renderSceneView;
    private double gamma;

    public PanelController(RenderConfiguration render, SceneConfiguration scene) {
        camera = new EditableCamera(render);
        objectList = scene.objects();
        lightSources = scene.lights();
        ambientColor = scene.ambient();
        backgroundColor = render.background();
        lineSceneView = createPreviewPanel();
        gamma = render.gamma();
    }

    private LineSceneView createPreviewPanel() {
        return new LineSceneView(camera, objectList);
    }

    private RenderSceneView createRenderPanel() {
        return new RenderSceneView();
    }

    public void setRenderConfiguration(RenderConfiguration renderConfiguration) {
        camera = new EditableCamera(renderConfiguration);
        backgroundColor = renderConfiguration.background();
    }
}
