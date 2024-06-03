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

}
