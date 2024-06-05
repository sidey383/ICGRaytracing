package ru.sidey383.render.camera;

import ru.sidey383.math.Vector3;

public record FinalCamera(
        Vector3 eye,
        Vector3 view,
        Vector3 up,
        Vector3 dir,
        Vector3 right,
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

    @Override
    public double height(int height, int width) {
        return this.height;
    }

    @Override
    public double width(int height, int width) {
        return this.height * width / height;
    }

    @Override
    public double width() {
        return height;
    }

    @Override
    public FinalCamera toFinalCamera() {
        return this;
    }

    @Override
    public EditableCamera toEditableCamera() {
        return new EditableCamera(eye, view, up, height, near, far);
    }

}
