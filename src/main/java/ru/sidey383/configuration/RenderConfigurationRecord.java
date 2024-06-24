package ru.sidey383.configuration;

import ru.sidey383.math.Vector;
import ru.sidey383.math.Vector3;

import java.util.Iterator;

import static ru.sidey383.ConfigurationUtility.*;

public record RenderConfigurationRecord(
        Vector3 background,
        double gamma,
        int traceDepth,
        Quality quality,
        Vector3 eye,
        Vector3 view,
        Vector3 up,
        double near,
        double far,
        double width,
        double height,
        int renderWidth,
        int renderHeight,
        boolean customSizes
) {

    public RenderConfigurationRecord(
            Vector3 background,
            double gamma,
            int traceDepth,
            Quality quality,
            Vector3 eye,
            Vector3 view,
            Vector3 up,
            double near,
            double far,
            double width,
            double height
    ) {
        this(background, gamma, traceDepth, quality, eye, view, up, near,far, width, height, 1000, 1000, false);
    }

    public static RenderConfigurationRecord parseConfiguration(String config) {
        Iterator<String> i = config.lines().iterator();
        int value = 0;
        Vector3 backgroundColor = null;
        double gamma = 0;
        int traceDepth = 0;
        Quality quality = null;
        Vector3 eye = null;
        Vector3 view = null;
        Vector3 up = null;
        Vector viewRange = null;
        Vector sizes = null;
        while (i.hasNext()) {
            String str = i.next();
            str = str.split("//")[0];
            if (str.isBlank()) continue;
            switch (value) {
                case 0 -> backgroundColor = readVector(str).mul(1.0/255.0);
                case 1 -> gamma = readValue(str);
                case 2 -> traceDepth = (int) readValue(str);
                case 3 -> quality = readQuality(str);
                case 4 -> eye = readVector(str);
                case 5 -> view = readVector(str);
                case 6 -> up = readVector(str);
                case 7 -> viewRange = readValuePair(str);
                case 8 -> sizes = readValuePair(str);
            }
            value++;
        }
        if (value < 8) throw new IllegalArgumentException("Not enough values");
        assert viewRange != null;
        assert sizes != null;
        if (viewRange.get(0) <= 0 || viewRange.get(1) <= 0 || sizes.get(0) <= 0 || sizes.get(1) <= 0)
            throw new IllegalArgumentException("View range and sizes must be positive");
        if (viewRange.get(0) > viewRange.get(1))
            throw new IllegalArgumentException("The near plane should be further than the far one");
        return new RenderConfigurationRecord(backgroundColor, gamma, traceDepth, quality, eye, view, up, viewRange.get(0), viewRange.get(1), sizes.get(0), sizes.get(1));
    }

    public String writeConfiguration() {
        return writeVector(background.mul(255)) + "\n" +
                gamma + "\n" +
                traceDepth + "\n" +
                writeQuality(quality) + "\n" +
                writeVector(eye) + "\n" +
                writeVector(view) + "\n" +
                writeVector(up) + "\n" +
                near + " " + far + "\n" +
                width + " " + height + "\n";
    }



}
