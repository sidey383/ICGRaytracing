package ru.sidey383.configuration;

import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.TriangleFigure;
import ru.sidey383.math.CalculationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class STLModelLoader {

    private final Vector3 diffuse;
    private final Vector3 specular;
    private final double power;

    public STLModelLoader(Vector3 diffuse, Vector3 specular, double power) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.power = power;
    }

    public List<TriangleFigure> readSTL(InputStream is) throws IOException {
        byte[] data = new byte[50];
        int read = is.read(data, 0, 40);
        if (read < 40)
            throw new IOException("Invalid file format");
        if (new String(data, 0, 5).equals("solid"))
            throw new IOException("Invalid file format");
        read = is.read(data, 0, 40);
        if (read < 40)
            throw new IOException("Invalid file format");
        read = is.read(data, 0, 4);
        if (read < 4)
            throw new IOException("Invalid file format");
        int count =  readValue(data, 0);
        List<TriangleFigure> figures = new ArrayList<>(count);
        while (is.read(data, 0, 50) == 50 && count-- > 0) {
            float[][] v = new float[4][3];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    v[i][j] = Float.intBitsToFloat(readValue(data, i * 12 + j * 4));
                }
            }
            Vector3 normal = new Vector3(v[0]);
            Vector3 a = new Vector3(v[1]);
            Vector3 b = new Vector3(v[2]);
            Vector3 c = new Vector3(v[3]);
            Vector3 rn = CalculationUtils.calculateNormal(a, b, c);
            if (normal.length() > 0.1) {
                if (normal.dot(rn) < 0) {
                    Vector3 tmp = a;
                    a = b;
                    b = tmp;
                }
            } else {
                normal = rn;
            }
            figures.add(new TriangleFigure(a, b, c, diffuse, specular, power));
        }
        return figures;

    }

    private int readValue(byte[] buffer, int pose) {
        return  (buffer[pose + 0] & 0xFF) | (buffer[pose + 1] & 0xFF) << 8 | (buffer[pose + 2] & 0xFF) << 16 | (buffer[pose + 3] & 0xFF) << 24;
    }

}
