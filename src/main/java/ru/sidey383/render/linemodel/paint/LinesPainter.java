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
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setPaint(Color.BLACK);
        drawOnlyLines(camera, suppliers, g, width, height);
    }


    public void drawOnlyLines(Camera camera, Collection<? extends LinesSupplier> suppliers, Graphics2D g, int width, int height) {
        Matrix transformation = getProjection(camera, width, height).multiply(getRotationMatrix(camera)).multiply(getTranslationMatrix(camera));
        List<Pair<Vector>> lines = new ArrayList<>();
        for (var supplier : suppliers) {
            lines.addAll(supplier.calculateLines(transformation));
        }
        for (Pair<Vector> p : lines) {
            Vector v1 = p.first();
            Vector v2 = p.second();
            if (v1.get(2) <= 0 && v2.get(2) <= 0)
                continue;
            double x1Pose = v1.get(0) / v1.get(3);
            double y1Pose = v1.get(1) / v1.get(3);
            double x2Pose = v2.get(0) / v2.get(3);
            double y2Pose = v2.get(1) / v2.get(3);
            if (v1.get(2) <= 0) {
                double t = -v1.get(2) / (v2.get(2) - v1.get(2));
                x1Pose = v1.get(0) + t * (v2.get(0) - v1.get(0));
                y1Pose = v1.get(1) + t * (v2.get(1) - v1.get(1));
            } else if (v2.get(3) <= 0) {
                double t = -v2.get(2) / (v1.get(2) - v2.get(2));
                x2Pose = v2.get(0) + t * (v1.get(0) - v2.get(0));
                y2Pose = v2.get(1) + t * (v1.get(1) - v2.get(1));
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
        return new PerspectiveProjectionMatrix(
                camera.height(height, width) / 2,
                camera.width(height, width) / 2,
                camera.near(), camera.far()
        );
    }

    private Matrix getRotationMatrix(Camera camera) {
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

    private Matrix getTranslationMatrix(Camera camera) {
        Vector3 eye = camera.eye();
        return MatrixTransformation.getTransposition(-eye.x(), -eye.y(), -eye.z());
    }


}
