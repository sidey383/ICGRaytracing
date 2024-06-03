package ru.sidey383.camera;

import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.math.Vector3;

public record FinalCamera(
        Vector3 pos, Vector3 up, Vector3 z, Vector3 right,
        double height, double near, double far
) implements Camera {

    public FinalCamera {
        if (height <= 0)
            throw new IllegalArgumentException("Height must be positive");
        if (near <= 0)
            throw new IllegalArgumentException("Near must be positive");
        if (far <= 0)
            throw new IllegalArgumentException("Far must be positive");
        if (far <= near)
            throw new IllegalArgumentException("Far must be greater than near");
    }

    public FinalCamera(RenderConfiguration configuration) {
        this(
                configuration.eye(),
                configuration.up().normalize(),
                configuration.view().sub(configuration.eye()).normalize(),
                configuration.up().cross(configuration.view().sub(configuration.eye()).normalize()).normalize(),
                configuration.height(),
                configuration.front(),
                configuration.back()
        );
    }

    @Override
    public FinalCamera toFinalCamera() {
        return this;
    }

    public EditableCamera toEditableCamera() {
        return new EditableCamera(pos, z, right, up, height, near, far);
    }

}
