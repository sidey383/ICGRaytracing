package ru.sidey383;

import ru.sidey383.configuration.Quality;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.linemodel.model.Pair;
import ru.sidey383.math.Vector;
import ru.sidey383.math.VectorRecord;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationUtility {
    private ConfigurationUtility(){}

    public static Vector3 readVector(String str) {
        String[] values = str.split("\\s+");
        if (values.length != 3) throw new IllegalArgumentException("Vector must have 3 components");
        return new Vector3(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]));
    }

    public static Pair<Vector3> readVectorPair(String str) {
        String[] values = str.split("\\s+");
        if (values.length != 6) throw new IllegalArgumentException("Vector pair must have 6 components");
        return new Pair<>(
                new Vector3(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])),
                new Vector3(Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[5]))
        );
    }

    public record MaterialInfo(Vector3 diffuse, Vector3 specular, double power) {
        public MaterialInfo {
            if (power < 0) throw new IllegalArgumentException("Power must be positive");
            if (diffuse.x() < 0 || diffuse.y() < 0 || diffuse.z() < 0)
                throw new IllegalArgumentException("Diffuse color must be positive");
            if (specular.x() < 0 || specular.y() < 0 || specular.z() < 0)
                throw new IllegalArgumentException("Specular color must be positive");
        }
    }

    public static MaterialInfo readMaterial(String str) {
        String[] values = str.split("\\s+");
        if (values.length != 7) throw new IllegalArgumentException("Material must have 8 components");
        return new MaterialInfo(
                new Vector3(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])).mul(1.0/255.0),
                new Vector3(Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[5])).mul(1.0/255.0),
                Double.parseDouble(values[6])
        );
    }

    public static String writeVector(Vector3 v) {
        return v.x() + " " + v.y() + " " + v.z();
    }

    public static String writeQuality(Quality q) {
        return q.name().toLowerCase();
    }


    public static double ranged(double v) {
        return Math.min(1, Math.max(0, v));
    }

    public static Vector3 ranged(Vector3 v) {
        return new Vector3(ranged(v.x()), ranged(v.y()), ranged(v.z()));
    }

    public static double readValue(String str) {
        return Double.parseDouble(str);
    }

    public static Quality readQuality(String str) {
        return Quality.getQuality(str.strip());
    }

    public static Vector readValuePair(String str) {
        String[] values = str.split("\\s+");
        if (values.length != 2) throw new IllegalArgumentException("Pair must have 2 components");
        return new VectorRecord(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
    }

    public static String readFile(Path p) throws IOException {
        try (InputStream is = Files.newInputStream(p)) {
            return new String(is.readAllBytes());
        }
    }

    public static void writeMaterial(StringBuilder sb, Vector3 diffuse, Vector3 specular, double power) {
        sb.append(diffuse.x() * 255).append(' ').append(diffuse.y() * 255).append(' ').append(diffuse.z() * 255).append(' ')
                .append(specular.x() * 255).append(' ').append(specular.y() * 255).append(' ').append(specular.z() * 255).append(' ')
                .append(power).append('\n');
    }

}
