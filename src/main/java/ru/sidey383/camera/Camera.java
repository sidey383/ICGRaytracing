package ru.sidey383.camera;

import ru.sidey383.math.Vector3;

public interface Camera {

    Vector3 pos();
    Vector3 z();
    Vector3 right();
    Vector3 up();
    double height();
    double near();
    double far();
    FinalCamera toFinalCamera();
    EditableCamera toEditableCamera();

    default double angleX() {
        return Math.atan2(right().get(1), right().get(0));
    }
    default double angleY() {
        return Math.atan2(right().get(2), Math.sqrt(right().get(0) * right().get(0) + right().get(1) * right().get(1)));
    }

    default double angleZ() {
        return Math.atan2(up().get(0), up().get(1));
    }
}
