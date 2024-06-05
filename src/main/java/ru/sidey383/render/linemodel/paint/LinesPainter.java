package ru.sidey383.render.linemodel.paint;

import ru.sidey383.render.camera.Camera;
import ru.sidey383.render.linemodel.model.LinesSupplier;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.math.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LinesPainter {

    public void createImage(Camera camera, Collection<? extends LinesSupplier> suppliers, Graphics2D g, int width, int height) {
        List<Pair<Vector>> lines = new ArrayList<>();
        for (var supplier : suppliers) {
            lines.addAll(supplier.getLines());
        }
        Matrix translation = getTranslationMatrix(camera);
        lines.replaceAll(vectorPair -> vectorPair.apply(translation::multiply));
//        Matrix rotation = getRotationMatrix(camera);
//        lines.replaceAll(vectorPair -> vectorPair.apply(rotation::multiply));
        Matrix projection = getProjection(camera, width, height);
        lines.replaceAll(vectorPair -> vectorPair.apply(projection::multiply));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setPaint(Color.BLACK);
        for (Pair<Vector> p : lines) {
            Vector v1 = p.first();
            Vector v2 = p.second();
            if (v1.get(3) == 0 || v2.get(3) == 0)
                continue;
            double x1Pose = v1.get(0) / v1.get(3);
            double y1Pose = v1.get(1) / v1.get(3);
            double x2Pose = v2.get(0) / v2.get(3);
            double y2Pose = v2.get(1) / v2.get(3);
            int x1 = (int) (width * x1Pose) + width / 2;
            int y1 = (height / 2) - (int) (height * y1Pose);
            int x2 = (int) (width * x2Pose) + width / 2;
            int y2 = (height / 2) - (int) (height * y2Pose);
            g.setStroke(new BasicStroke(2));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private Matrix getProjection(Camera camera, int width, int height) {
        synchronized (this) {
            return new PerspectiveProjectionMatrix(
                    camera.height(height, width) / 2,
                    camera.width(height, width) / 2,
                    camera.near(), camera.far()
            );
        }
    }

    private Matrix getRotationMatrix(Camera camera) {
        Vector3 dir = camera.dir();
        double cos = dir.dot(Vector3.Z) / dir.length();
        Vector3 axis = dir.cross(Vector3.Z);
        QuaternionRotation firstRotation = new QuaternionRotation(axis, Math.acos(cos));
        Vector3 up = camera.up();
        up = firstRotation.mult(up);
        cos = up.dot(Vector3.Y) / up.length();
        axis = up.cross(Vector3.Y);
        QuaternionRotation second = new QuaternionRotation(axis, Math.acos(cos));
        return firstRotation.mult(second).toRotationMatrix();
    }

    private Matrix getTranslationMatrix(Camera camera) {
        Vector3 eye = camera.eye();
        return MatrixTransformation.getTransposition(-eye.get(0), -eye.get(1), -eye.get(2));
    }


}
