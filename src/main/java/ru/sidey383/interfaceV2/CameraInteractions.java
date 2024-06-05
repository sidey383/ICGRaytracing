package ru.sidey383.interfaceV2;

public interface CameraInteractions {

    void rotateCamera(double xRot, double yRot);

    int screenWidth();

    int screenHeight();

    void goForward(double val);

    void move(double x, double y);

    void zoom(double value);

}
