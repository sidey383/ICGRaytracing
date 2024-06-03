package ru.sidey383.inerface;

import ru.sidey383.math.Vector3;

public interface FrameInteraction {

    void rotateCamera(double xRot, double yRot);

    int screenWidth();

    int screenHeight();

    void goForward(double val);

    void move(double x, double y);

    void zoom(double value);

}
