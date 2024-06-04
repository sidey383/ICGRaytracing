package ru.sidey383.render.raytrace;

import ru.sidey383.camera.FinalCamera;
import ru.sidey383.configuration.Quality;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.LightSource;

import java.util.List;

public record RaytraceConfiguration(
        int width,
        int height,
        FinalCamera camera,
        Quality quality,
        List<? extends RaytraceObject> objectList,
        List<? extends LightSource> lights,
        int depth, Vector3 ambient, Vector3 background
) {}
