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
        Matrix rotation = getRotationMatrix(camera);
        lines.replaceAll(vectorPair -> vectorPair.apply(rotation::multiply));
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
            double z1Pose = v1.get(2);
            double z2Pose = v2.get(2);
            if (z1Pose != z2Pose) {

                if (z1Pose < 0) {
                    if (z2Pose <= 0)
                        continue;
                    x1Pose = (x1Pose * z2Pose + x2Pose * -z1Pose) / (z2Pose - z1Pose);
                    y1Pose = (y1Pose * z2Pose + y2Pose *  -z1Pose) / (z2Pose - z1Pose);
                    z2Pose = 0;
                }
                if (z2Pose < 0) {
                    x2Pose = (x1Pose * -z2Pose + x2Pose * z1Pose) / (z1Pose - z2Pose);
                    y2Pose = (y1Pose * -z2Pose + y2Pose * z1Pose) / (z1Pose - z2Pose);
                    z2Pose = 0;
                }
            } else {
                if (z1Pose < 0)
                    continue;
            }
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
        QuaternionRotation first = getTargetRotation(camera.up(), Vector3.Y);
        Matrix rotation = first.toRotationMatrix();
        QuaternionRotation second = getTargetRotation(
                Vector3Record.crop(rotation.multiply(camera.right().toVector4())),
                Vector3.X
        );
        return rotation.multiply(second.toRotationMatrix());
    }

    private static QuaternionRotation getTargetRotation(Vector3 from, Vector3 to) {
        Vector3 cross = from.cross(to);
        double w = from.length();
        if (w < 1e-30)
            return new QuaternionRotation(1, 0, 0, 0);
        w = w*w + from.dot(to);
        return new QuaternionRotation(cross.x(), cross.y(), cross.z(), w);
    }

    private Matrix getTranslationMatrix(Camera camera) {
        Vector3 eye = camera.eye();
        return MatrixTransformation.getTransposition(-eye.get(0), -eye.get(1), -eye.get(2));
    }


}
