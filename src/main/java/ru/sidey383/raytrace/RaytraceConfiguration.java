package ru.sidey383.raytrace;

import ru.sidey383.camera.Camera;
import ru.sidey383.camera.FinalCamera;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.math.Vector3;
import ru.sidey383.objects.DrawableObject;

import java.util.List;
import java.util.stream.Collectors;

public record RaytraceConfiguration(
        FinalCamera camera,
        List<RaytraceObject> objectList,
        List<LightSource> lights,
        int depth, Vector3 ambient
) {

    public RaytraceConfiguration {}

}
