package ru.sidey383.render.camera;

import ru.sidey383.configuration.RenderConfigurationRecord;
import ru.sidey383.math.QuaternionRotation;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.Figure;

import java.util.List;

public class EditableCamera implements Camera {

    private Vector3 eye;
    private Vector3 view;
    private Vector3 up;
    private double height;
    private double near;
    private double far;

    public EditableCamera(RenderConfigurationRecord configuration) {
        this.eye = configuration.eye();
        this.view = configuration.view();
        Vector3 dir = view.sub(eye);
        Vector3 right = dir.cross(configuration.up());
        this.up = right.cross(dir).normalize();
        this.height = configuration.height();
        this.near = configuration.near();
        this.far = configuration.far();
    }

    private static Vector3 fixUp(Vector3 up, Vector3 view, Vector3 eye) {
        Vector3 dir = view.sub(eye);
        Vector3 right = dir.cross(up);
        return right.cross(dir).normalize();

    }

    public EditableCamera(List<? extends Figure> objects, int width, int height) {
        if (objects.isEmpty()) {
            this.eye = new Vector3(0, 0, -10);
            this.view = new Vector3(0, 0, 0);
            this.up = Vector3.Z;
            this.height = 1;
            this.near = 1;
            this.far = 30;
            return;
        }
        Vector3 min = objects.get(0).min();
        Vector3 max = objects.get(0).max();
        for (Figure object : objects) {
            min = new Vector3(
                    Math.min(min.x(), object.min().x()),
                    Math.min(min.y(), object.min().y()),
                    Math.min(min.z(), object.min().z())
            );
            max = new Vector3(
                    Math.max(max.x(), object.max().x()),
                    Math.max(max.y(), object.max().y()),
                    Math.max(max.z(), object.max().z())
            );
        }
        this.view = min.add(max).mul(0.5);
        Vector3 add = max.sub(min).mul(0.05);
        min = min.add(add.mul(-1));
        max = max.add(add);
        this.up = Vector3.Z;
        this.eye = new Vector3(
                min.x() - Math.max(max.z() - min.z(), max.y() - min.y()) * Math.sqrt(3) / 2,
                this.view.y(),
                this.view.z()
        );
        this.near = (min.x() - this.eye.x()) / 2;
        this.far = max.x() - this.eye.x() + (max.x() - min.x()) / 2;
        double vheight = (max.z() - min.z()) / 2;
        double vwidth = (max.y() - min.y()) / 2;
        this.height = Math.max(vheight, vwidth * height / width);
    }

    public EditableCamera(Vector3 eye, Vector3 view, Vector3 up, double height, double near, double far) {
        this.eye = eye;
        this.view = view;
        Vector3 dir = view.sub(eye);
        Vector3 right = dir.cross(up);
        this.up = right.cross(dir).normalize();
        this.height = height;
        this.near = near;
        this.far = far;
    }

    public void goForward(double val) {
        if (val > 0) {
            Vector3 dir = view.sub(eye);
            double dirLen = dir.length();
            if (dirLen - val < 0.5) {
                val = val - 0.5;
            }
            dir = dir.normalize();
            eye = eye.add(dir.mul(val));
        } else {
            eye = eye.add(dir().mul(val));
        }
    }

    public void move(double x, double y) {
        Vector3 move = up.mul(y).add(right().mul(x));
        eye = eye.add(move);
        view = view.add(move);
    }

    public void zoom(double value) {
        near = Math.min(far/2, Math.max(0.1, near * Math.pow(2, value)));
    }

    public void rotate(double xRotation, double yRotation) {
        QuaternionRotation rot = QuaternionRotation.IDENTITY;
        if (xRotation != 0)
            rot = rot.mult(new QuaternionRotation(up(), xRotation));
        if (yRotation != 0)
            rot = rot.mult(new QuaternionRotation(right(), -yRotation));
        Vector3 e = eye.sub(view);
        e = rot.rotate(e);
        eye = e.add(view);
        up = rot.rotate(up).normalize();
        up = fixUp(up, view, eye);
    }

    @Override
    public Vector3 eye() {
        return eye;
    }

    @Override
    public Vector3 dir() {
        return view.sub(eye).normalize();
    }

    @Override
    public Vector3 view() {
        return view;
    }

    @Override
    public Vector3 right() {
        return dir().cross(up).normalize();
    }

    @Override
    public Vector3 up() {
        return up;
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
    public double height() {
        return height;
    }

    @Override
    public double width() {
        return height;
    }

    @Override
    public double near() {
        return near;
    }

    @Override
    public double far() {
        return far;
    }

    public FinalCamera toFinalCamera() {
        return new FinalCamera(eye, view, up, dir(), right(), height, near, far);
    }

    @Override
    public EditableCamera toEditableCamera() {
        return this;
    }

    @Override
    public String toString() {
        return "EditableCamera{" +
               "eye=" + eye +
               ", view=" + view +
               ", up=" + up +
               ", height=" + height +
               ", near=" + near +
               ", far=" + far +
               '}';
    }
}
