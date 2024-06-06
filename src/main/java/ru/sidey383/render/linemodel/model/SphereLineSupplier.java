package ru.sidey383.render.linemodel.model;

import ru.sidey383.math.*;

import java.util.ArrayList;
import java.util.List;

public class SphereLineSupplier implements LinesSupplier {

    private final Vector[][] vectors;

    private final int partCount;

    private final int partSize;

    private final int m;

    private final int m1;

    private SphereLineSupplier(int partCount, int partSize, int m, int m1) {
        this.partSize = partSize;
        this.partCount = partCount;
        this.m = m;
        this.m1 = m1;
        vectors = new Vector[m * m1][];
        for (int i = 0; i < m * m1; i++) {
            vectors[i] = i % m1 == 0 ? new Vector[partCount * partSize + 1] : new Vector[partCount + 1];
        }
    }

    public SphereLineSupplier(Vector3 position, double radius) {
        this(20, 20, 20, 20);
        calculateBaseVectors(position, radius);
    }

    private void calculateBaseVectors(Vector3 center, double radius) {
        List<Vector> xyPoints = new ArrayList<>();
        for (int i = 0; i < partCount * partSize + 1; i++) {
            xyPoints.add(new VectorRecord(
                     radius*Math.cos(Math.PI * 2 * i / (partCount * partSize)),
                    radius*Math.sin(Math.PI * 2 * i / (partCount * partSize)),
                    0,
                    1
            ));
        }
        for (int angle = 0; angle < m * m1; angle++) {
            Matrix rotation = MatrixTransformation.X.createRotationMatrix(angle * Math.PI * 2 / (m * m1));
            if (angle % m1 == 0) {
                for (int part = 0; part < partCount; part++) {
                    for (int num = 0; num < partSize; num++) {
                        setValue(
                                angle,
                                part * partSize + num,
                                rotation.multiply(
                                        new VectorRecord(
                                                xyPoints.get(part * partSize + num).get(0),
                                                xyPoints.get(part * partSize + num).get(1),
                                                0,
                                                1
                                        )
                                )
                        );
                    }
                    if (part == partCount - 1) {
                        setValue(
                                angle,
                                partCount * partSize,
                                rotation.multiply(
                                        new VectorRecord(
                                                xyPoints.get(partCount * partSize).get(0),
                                                xyPoints.get(partCount * partSize).get(1),
                                                0,
                                                1
                                        )
                                )
                        );
                    }
                }
            } else {
                for (int part = 0; part <= partCount; part++) {
                    setValue(
                            angle,
                            part * partSize,
                            rotation.multiply(
                                    new VectorRecord(
                                            xyPoints.get(part * partSize).get(0),
                                            xyPoints.get(part * partSize).get(1),
                                            0,
                                            1
                                    )
                            )
                    );
                }
            }
        }
        Matrix matrix = MatrixTransformation.getTransposition(center.x(), center.y(), center.z());
        for (int rot = 0; rot < m1 * m; rot++) {
            if (rot % m1 == 0) {
                for (int i = 0; i <= partCount * partSize; i++) {
                    setValue(rot, i, matrix.multiply(getValue(rot, i)));
                }
            } else {
                for (int i = 0; i <= partCount; i++) {
                    setValue(rot, i * partSize, matrix.multiply(getValue(rot, i * partSize)));
                }
            }
        }
    }

    public SphereLineSupplier applyMatrix(Matrix matrix) {
        SphereLineSupplier transformed = new SphereLineSupplier(partCount, partSize, m, m1);
        for (int rot = 0; rot < m1 * m; rot++) {
            if (rot % m1 == 0) {
                for (int i = 0; i <= partCount * partSize; i++) {
                    transformed.setValue(rot, i, matrix.multiply(getValue(rot, i)));
                }
            } else {
                for (int i = 0; i <= partCount; i++) {
                    transformed.setValue(rot, i * partSize, matrix.multiply(getValue(rot, i * partSize)));
                }
            }
        }
        return transformed;
    }

    @Override
    public List<Pair<Vector>> calculateLines(Matrix transformation) {
        return applyMatrix(transformation).getLines();
    }

    public List<Pair<Vector>> getLines() {
        List<Pair<Vector>> lines = new ArrayList<>();
        for (int num = 0; num < this.getFormativeCount(); num ++) {
            for (int i = 1; i < this.getFormativeSize(); i++) {
                lines.add(new Pair<>(
                        getFormativePoint(num, i - 1),
                        getFormativePoint(num, i)
                ));
            }
        }
        for (int num = 0; num < getCircleCount(); num ++) {
            for (int i = 0; i <= getCircleSize(); i++) {
                lines.add(new Pair<>(
                        getCirclePoint(num, i),
                        getCirclePoint(num, i + 1)
                ));
            }
        }
        return lines;
    }

    public int getCircleCount() {
        return partCount + 1;
    }

    public int getCircleSize() {
        return m * m1;
    }

    public Vector getCirclePoint(int num, int i) {
        return getValue(i, num * partSize);
    }

    public int getFormativeCount() {
        return m;
    }

    public int getFormativeSize() {
        return partCount * partSize + 1;
    }

    public Vector getFormativePoint(int num, int i) {
        return getValue(num * m1, i);
    }

    private Vector getValue(int rot, int pos) {
        return vectors[rot % (m1 * m)][getArrayPose(rot, pos)];
    }

    private void setValue(int rot, int pos, Vector vector) {
        int i = rot % (m1 * m);
        int j = getArrayPose(rot, pos);
        Vector[] l = vectors[i];
        l[j] = vector;
    }

    private int getArrayPose(int rot, int pos) {
        if (rot % m1 == 0) {
            if (pos > partCount * partSize)
                throw new IllegalArgumentException("Index out of range");
            return pos;
        } else {
            pos = pos / partSize;
            if (pos > partCount)
                throw new IllegalArgumentException("Index out of range");
            return pos;
        }
    }

}
