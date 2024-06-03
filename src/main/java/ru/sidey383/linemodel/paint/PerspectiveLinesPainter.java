package ru.sidey383.linemodel.paint;

import ru.sidey383.camera.Camera;
import ru.sidey383.camera.EditableCamera;
import ru.sidey383.linemodel.GradientCreator;
import ru.sidey383.linemodel.LinesPainter;
import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.linemodel.model.Pair;
import ru.sidey383.math.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PerspectiveLinesPainter implements LinesPainter {

    private final Camera camera;

    public PerspectiveLinesPainter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void createImage(Collection<LinesSupplier> suppliers, Graphics2D g, int width, int height) {
        List<Pair<Vector>> lines = new ArrayList<>();
        for (var supplier : suppliers) {
            lines.addAll(supplier.createLines());
        }
        Matrix translation = getTranslationMatrix();
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).apply(translation::multiply));
        }
        Matrix rotation = getRotationMatrix();
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).apply(rotation::multiply));
        }
        Matrix projection = getProjection(width, height);
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).apply(projection::multiply));
        }
        GradientCreator gradientCreator = new GradientCreator(
                lines.stream().mapMultiToDouble((v, c) -> {
                    c.accept(v.first().get(2) / v.first().get(3));
                    c.accept(v.second().get(2) / v.second().get(3));
                }).toArray()
        );
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        for (Pair<Vector> p : lines) {
            Vector v1 = p.first();
            Vector v2 = p.second();
            double x1Pose = v1.get(0) / (2 * v1.get(3));
            double y1Pose = v1.get(1) / (2 * v1.get(3));
            double x2Pose = v2.get(0) / (2 * v2.get(3));
            double y2Pose = v2.get(1) / (2 * v2.get(3));
//            double z1Pose = v1.get(2) / v1.get(3);
//            double z2Pose = v2.get(2) / v2.get(3);
//            if (z1Pose != z2Pose) {
//
//                if (z1Pose < -1) {
//                    if (z2Pose <= -1)
//                        continue;
//                    x1Pose = (x1Pose * (z2Pose + 1) + x2Pose * (1 + z1Pose)) / (z1Pose + z2Pose);
//                    y1Pose = (y1Pose * (z2Pose + 1) + y2Pose * (1 + z1Pose)) / (z1Pose + z2Pose);
//                    z1Pose = -1;
//                }
//                if (z2Pose < -1) {
//                    x2Pose = (x1Pose * (z2Pose + 1) + x2Pose * (1 + z1Pose)) / (z1Pose + z2Pose);
//                    y2Pose = (y1Pose * (z2Pose + 1) + y2Pose * (1 + z1Pose)) / (z1Pose + z2Pose);
//                    z2Pose = -1;
//                }
//                if (z1Pose > 1) {
//                    if (z2Pose >= 1)
//                        continue;
//                    x1Pose = (x1Pose * (z1Pose - 1) + x2Pose * (1 - z2Pose)) / (z1Pose + z2Pose);
//                    y1Pose = (y1Pose * (z1Pose - 1) + y2Pose * (1 - z2Pose)) / (z1Pose + z2Pose);
//                    z1Pose = 1;
//                }
//                if (z2Pose > 1) {
//                    x2Pose = (x1Pose * (z2Pose - 1) + x2Pose * (1 - z1Pose)) / (z1Pose + z2Pose);
//                    y2Pose = (y1Pose * (z2Pose - 1) + y2Pose * (1 - z1Pose)) / (z1Pose + z2Pose);
//                    z2Pose = 1;
//                }
//            } else {
//                if (z1Pose < -1 || z1Pose > 1)
//                    continue;
//            }
            int x1 = (int) (width * x1Pose) + width / 2;
            int y1 = (height / 2) - (int) (height * y1Pose);
            int x2 = (int) (width * x2Pose) + width / 2;
            int y2 = (height / 2) - (int) (height * y2Pose);
            g.setPaint(
                    new GradientPaint(
                            x1, y1, gradientCreator.getDotColor(v1.get(2) / v1.get(3)),
                            x2, y2, gradientCreator.getDotColor(v2.get(2) / v2.get(3))
                    )
            );
            g.setStroke(new BasicStroke(2));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private Matrix getProjection(int width, int height) {
        synchronized (this) {
            return new PerspectiveProjectionMatrix(
                    camera.height(),
                    camera.height() * width / height,
                    camera.near(), camera.far()
            );
        }
    }

    private Matrix getRotationMatrix() {
        Vector3 z = camera.z();
        double len = z.length();
        double xAngle = Math.acos(z.get(0) / len);
        double yAngle = Math.acos(z.get(1) / len);
        double zAngle = Math.acos(z.get(2) / len);
        Matrix xRot = MatrixTransformation.X.createRotationMatrix(xAngle);
        Matrix yRot = MatrixTransformation.Y.createRotationMatrix(yAngle);
        Matrix zRot = MatrixTransformation.Z.createRotationMatrix(zAngle);
        return xRot.multiply(yRot).multiply(zRot);
    }

    private Matrix getTranslationMatrix() {
        Vector3 pose = camera.pos();
        return MatrixTransformation.getTransposition(-pose.get(0), -pose.get(1), -pose.get(2));
    }


}
