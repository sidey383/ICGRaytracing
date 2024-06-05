package ru.sidey383.inerface;

import ru.sidey383.inerface.view.PreviewSceneView;
import ru.sidey383.inerface.view.RenderSceneView;

import java.util.Optional;

public interface SceneHolder {

    Optional<RenderSceneView> getRenderSceneView();

    PreviewSceneView getPreviewSceneView();

}
