package ru.sidey383.render.camera;

import ru.sidey383.math.Vector3;

public interface Camera {

    Vector3 eye();
    Vector3 dir();
    Vector3 view();
    Vector3 right();
    Vector3 up();
    double height(int height, int width);
    double width(int height, int width);
    double height();
    double width();
    double near();
    double far();
    FinalCamera toFinalCamera();
    EditableCamera toEditableCamera();
}
