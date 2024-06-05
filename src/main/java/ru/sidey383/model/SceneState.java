package ru.sidey383.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.math.Vector3Record;
import ru.sidey383.render.objects.BoxFigure;
import ru.sidey383.render.objects.Figure;
import ru.sidey383.render.objects.LightSource;
import ru.sidey383.render.objects.SphereFigure;

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
                        new Vector3Record(0, 0, 0),
                        1,
                        new Vector3Record(1, 1, 1),
                        new Vector3Record(0.5, 0.5, 0.5),
                        10
                ),
                new BoxFigure(
                        new Vector3Record(-2, -2, -2),
                        new Vector3Record(2, -2, 2),
                        new Vector3Record(1, 1, 1),
                        new Vector3Record(0.5, 0.5, 0.5),
                        10
                )
        );
        light = List.of(
                new LightSource(
                        new Vector3Record(0, 5, -5),
                        new Vector3Record(0, 1, 0)
                )
        );
    }
}
