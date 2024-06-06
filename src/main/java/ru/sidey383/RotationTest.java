package ru.sidey383;

import ru.sidey383.math.Matrix;
import ru.sidey383.math.MatrixRecord;
import ru.sidey383.math.QuaternionRotation;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.camera.Camera;
import ru.sidey383.render.camera.EditableCamera;

public class RotationTest {

    public static void main(String[] args) {
        Camera camera = new EditableCamera(
                new Vector3(7.2829208477174765, 4.887254988993711, -4.803519813474213),
                new Vector3(0, 0, 0),
                new Vector3(0.02327058093963763, 0.6829280544102475, 0.7301148899742874),
                1, 1, 30
        );
        Matrix rotation = getRotationMatrix(camera);
        System.out.println(rotation);
        System.out.println("Expect 0, 0, -1 " + rotation.multiply(camera.dir().toVector4()));
        System.out.println("Expect 0, 1, 0 " + rotation.multiply(camera.up().toVector4()));
        System.out.println("Expect 1, 0, 0 " + rotation.multiply(camera.right().toVector4()));
        System.out.println(new QuaternionRotation(Vector3.X, Math.PI / 2).rotate(Vector3.Y));
    }

    public static Matrix getRotationMatrix(Camera camera) {
        Vector3 dir = camera.dir();
        Vector3 up = camera.up();
        Vector3 right = camera.right();
        return new MatrixRecord(new double[][]{
                {right.x(), right.y(), right.z(), 0},
                {up.x(), up.y(), up.z(), 0},
                {dir.x(), dir.y(), dir.z(), 0},
                {0, 0, 0, 1}
        });
    }

}
