package ru.sidey383.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.render.camera.EditableCamera;

@Getter
public class ApplicationParameters {
    @NotNull
    @Setter
    private EditableCamera camera;
    @NotNull
    private final RaytraceSettings raytraceSettings;
    @NotNull
    private final SceneState sceneState;

    public ApplicationParameters() {
        sceneState = new SceneState();
        raytraceSettings = new RaytraceSettings();
        camera = new EditableCamera(sceneState.getObjects(), 200, 200);
    }

    public void cameraInit(int width, int height) {
        camera = new EditableCamera(sceneState.getObjects(), width, height);
    }

}
