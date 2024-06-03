package ru.sidey383.camera;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.math.Vector3;

@Getter
@Accessors(fluent = true)
public class EditableCamera implements Camera {

    private Vector3 pos;
    private Vector3 z;
    private Vector3 right;
    private Vector3 up;
    private double height;
    private double near;
    private double far;

    public EditableCamera(Vector3 pos, Vector3 z, Vector3 right, Vector3 up, double height, double near, double far) {
        this.pos = pos;
        this.z = z;
        this.right = right;
        this.up = up;
        this.height = height;
        this.near = near;
        this.far = far;
    }

    public EditableCamera(RenderConfiguration configuration) {
        this.pos = configuration.eye();
        this.up = configuration.up().normalize();
        this.z = configuration.view().sub(this.pos).normalize();
        this.right = configuration.up().cross(this.z).normalize();
        this.height = configuration.height();
        this.near = configuration.front();
        this.far = configuration.back();
    }

    public void goForward(double val) {
        pos = pos.add(z.mul(val));
    }

    public void move(double x, double y) {
        pos = pos.add(right.mul(x)).add(up.mul(y));
    }

    public void zoom(double value) {
        near = near * Math.pow(1.01, value);
    }

    public void rotate(double xRotation, double yRotation) {
        Vector3 newZ = z.rotate(right, xRotation).rotate(up, yRotation).normalize();
        Vector3 newRight = right.rotate(up, yRotation).normalize();
        Vector3 newUp = newZ.cross(newRight).normalize();
        this.z = newZ;
        this.right = newRight;
        this.up = newUp;
    }

    public FinalCamera toFinalCamera() {
        return new FinalCamera(pos, up, z, right, height, near, far);
    }

    @Override
    public EditableCamera toEditableCamera() {
        return this;
    }

}
