package ru.sidey383.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.configuration.Quality;
import ru.sidey383.math.Vector3;

@Getter
@Setter
public class RaytraceSettings {
    @NotNull
    private Vector3 background;
    @NotNull
    private Vector3 ambient;
    @NotNull
    private Quality quality;
    private double gamma;
    private int traceDeep;
    private boolean isCustomSize;
    private int renderWidth;
    private int renderHeight;

    public RaytraceSettings() {
        background = new Vector3(0, 0, 0.5);
        ambient = new Vector3(0.1, 0.1, 0.1);
        quality = Quality.NORMAL;
        gamma = 1;
        traceDeep = 3;
        isCustomSize =  false;
        renderWidth = 1000;
        renderHeight = 1000;
    }

}
