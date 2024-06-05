package ru.sidey383.render.linemodel;

import java.awt.*;
import java.util.Arrays;

public class GradientCreator {
    private final double min;
    private final double max;

    public GradientCreator(double[] distances) {
        max = Arrays.stream(distances).max().orElse(0);
        min = Arrays.stream(distances).min().orElse(1);
    }

    public GradientCreator(double min, double max) {
        this.max = max;
        this.min = min;
    }

    public Color getDotColor(double value) {
        double distance = (value - min) / (max - min);
        return new Color(normalize(distance), normalize(distance), 128);
    }

    private int normalize(double val) {
        return Math.min(255, Math.max(0, (int) val));
    }

}