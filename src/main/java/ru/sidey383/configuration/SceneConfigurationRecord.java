package ru.sidey383.configuration;

import ru.sidey383.ConfigurationUtility;
import ru.sidey383.ParseIterator;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.render.objects.*;
import ru.sidey383.render.objects.LightSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.sidey383.ConfigurationUtility.*;

public record SceneConfigurationRecord(Vector3 ambient, List<LightSource> lights, List<Figure> objects) {

    public static SceneConfigurationRecord parseConfiguration(String str) {
        Iterator<String> lines = new ParseIterator(str.lines().iterator());
        Vector3 ambient;
        if (!lines.hasNext())
            throw new IllegalArgumentException("No ambient light");
        ambient = ConfigurationUtility.ranged(readVector(lines.next()).mul(1.0/255.0));
        int lightCount;
        if (!lines.hasNext())
            throw new IllegalArgumentException("No light count");
        lightCount = (int) readValue(lines.next());
        List<LightSource> lights = new ArrayList<>();
        for (int i = 0; i < lightCount; i++) {
            Pair<Vector3> pair = readVectorPair(lines.next());
            lights.add(new LightSource(pair.first(), pair.second().mul(1.0/255.0)));
        }
        List<Figure> objects = new ArrayList<>();
        while (lines.hasNext()) {
            String start = lines.next();
            String[] vals = start.split("\\s+");
            if (vals.length != 4)
                throw new IllegalArgumentException("Figure header must have 4 values");
            String type = vals[0];
            Vector3 first = new Vector3Record(Double.parseDouble(vals[1]), Double.parseDouble(vals[2]), Double.parseDouble(vals[3]));
            if (type.equalsIgnoreCase("sphere")) {
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No radius for sphere");
                double radius = readValue(lines.next());
                ConfigurationUtility.MaterialInfo material = ConfigurationUtility.readMaterial(lines.next());
                objects.add(new SphereFigure(first, radius, material.diffuse(), material.specular(), material.power()));
            } else if (type.equalsIgnoreCase("box")) {
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No second point for box");
                Vector3 second = readVector(lines.next());
                ConfigurationUtility.MaterialInfo material = ConfigurationUtility.readMaterial(lines.next());
                objects.add(new BoxFigure(first, second, material.diffuse(), material.specular(), material.power()));
            } else if (type.equalsIgnoreCase("triangle")) {
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No second point for triangle");
                Vector3 second = readVector(lines.next());
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No third point for triangle");
                Vector3 third = readVector(lines.next());
                ConfigurationUtility.MaterialInfo material = ConfigurationUtility.readMaterial(lines.next());
                objects.add(new TriangleFigure(first, second, third, material.diffuse(), material.specular(), material.power()));
            } else if (type.equalsIgnoreCase("quadrangle")) {
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No second point for quadrangle");
                Vector3 second = readVector(lines.next());
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No third point for quadrangle");
                Vector3 third = readVector(lines.next());
                if (!lines.hasNext())
                    throw new IllegalArgumentException("No fourth point for quadrangle");
                Vector3 fourth = readVector(lines.next());
                ConfigurationUtility.MaterialInfo material = ConfigurationUtility.readMaterial(lines.next());
                objects.add(new QuadrangleFigure(first, second, third, fourth, material.diffuse(), material.specular(), material.power()));
            } else {
                throw new IllegalArgumentException("Unknown figure type: " + type);
            }
        }

        return new SceneConfigurationRecord(ambient, lights, objects);

    }

}
