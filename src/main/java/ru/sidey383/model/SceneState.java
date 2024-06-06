package ru.sidey383.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.*;

import java.util.List;

@Getter
@Setter
public class SceneState {
    @NotNull
    private List<? extends Figure> objects;
    @NotNull
    private List<? extends LightSource> light;

    public SceneState() {
        objects = List.of(
                new SphereFigure(
                        new Vector3(0, 0, 0),
                        1,
                        new Vector3(1, 1, 1),
                        new Vector3(0.5, 0.5, 0.5),
                        10
                ),
                new QuadrangleFigure(
                        new Vector3(-1, -1, -1),
                        new Vector3(1, -1, -1),
                        new Vector3(1, 1, -1),
                        new Vector3(-1, 1, -1),
                        new Vector3(1, 1, 1),
                        new Vector3(0.5, 0.5, 0.5),
                        10

                )
        );
        light = List.of(
                new LightSource(
                        new Vector3(-40, 0, 0),
                        new Vector3(0, 1, 0)
                )
        );
    }
}
